package com.formacionbdi.springboot.app.oauth.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@RefreshScope
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter{
	
	@Autowired
	private Environment env;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private InfoAdicionalToken infoAdicionalToken;

	// Configurar la seguridad de los endpoints para generar y validar el token
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll()")   // ermite autentiar con la ruta  POST:/oauth/token
		.checkTokenAccess("isAuthenticated()");
		;
	}

	// Configurar los clientes que se van a conectar a nuestro microservicios
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
		//.withClient("frontendapp")   // Es un identificador de la aplicacion
		//.secret(passwordEncoder.encode("12345")) // Es la clave de la aplicacion, no del usuario
		
		.withClient(env.getProperty("config.security.oauth.client.id"))   // Es un identificador de la aplicacion
		.secret(passwordEncoder.encode(env.getProperty("config.security.oauth.client.secret"))) // Es la clave de la aplicacion, no del usuario
		
		.scopes("read", "write")   // alcance de la aplicacion
		.authorizedGrantTypes("password", "refresh_token")   // Como se va a obtener el token
		.accessTokenValiditySeconds(3600)  // Tiempo de validez del token
		.refreshTokenValiditySeconds(3600);
		
		/* para agregar otra aplicacion:
		.add()
		.withClient("androidapp")   // Es un identificador de la aplicacion
		.secret(passwordEncoder.encode("12345")) // Es la clave de la aplicacion, no del usuario
		.scopes("read", "write")   // alcance de la aplicacion
		.authorizedGrantTypes("password", "refresh_token")   // Como se va a obtener el token
		.accessTokenValiditySeconds(3600)  // Tiempo de validez del token
		.refreshTokenValiditySeconds(3600)
		*/
		
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(infoAdicionalToken, accessTokenConverter()));
		endpoints.authenticationManager(authenticationManager)
		.tokenStore(tokenStore())
		.accessTokenConverter(accessTokenConverter())
		.tokenEnhancer(tokenEnhancerChain);
		
	}


	// ALmacenar el Token
	@Bean
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	// Convertir el Token
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
		//tokenConverter.setSigningKey("algun_codigo:secreto_aeiou");   // Para firmar el token y luego validarlo
		tokenConverter.setSigningKey(env.getProperty("config.security.oauth.jwt.key"));   // Para firmar el token y luego validarlo
		return tokenConverter;
	}
	
	
}
