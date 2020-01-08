package com.formacionbdi.springboot.app.oauth.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.formacionbdi.springboot.app.oauth.client.UsuarioFeignClient;
import com.formacionbdi.springboot.app.usuarioscommons.models.entity.Usuario;

import brave.Tracer;
import feign.FeignException;

@Service
public class UsuarioService implements IUsuarioService, UserDetailsService {

	private Logger log = LoggerFactory.getLogger(UsuarioService.class);

	@Autowired
	private UsuarioFeignClient client;

	@Autowired
	private Tracer tracer;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		try {

			Usuario usuario = client.findByUsername(username);

			// Convierte lista de nombres roles a una lista Generics de GrantedAuthority
			// Peek, por cada rol mostrar el nombre en el log. El rol ya está convertido a
			// Authority
			List<GrantedAuthority> authorities = usuario.getRoles().stream() // API de Flujo
					.map(role -> new SimpleGrantedAuthority(role.getNombre())) // Convierte el flujo de roles (usar
																				// operador lambda)
					.peek(authority -> log.info("Role: " + authority.getAuthority())) // Operador que toma el dato
																						// convertido, en este caso un
																						// Authority (rol) que lo
																						// muestra en el Log
					.collect(Collectors.toList()); // Convierte stream (flujo) a List

			// Implementacion larga, sin Stream()
			/*
			 * List<GrantedAuthority> authorities2 = new ArrayList<GrantedAuthority>();
			 * List<Role> roles = usuario.getRoles(); for (int i=0; i<= roles.size(); i++) {
			 * String rolNombre = roles.get(i).getNombre(); SimpleGrantedAuthority authority
			 * = new SimpleGrantedAuthority(rolNombre); authorities2.add(authority);
			 * log.info("Role IN : " + roles.get(i).getNombre()); log.info("Role OUT: " +
			 * authority.getAuthority()); }
			 */

			log.info("Usuario autenticado : " + username);

			return new User(usuario.getUsername(), usuario.getPassword(), usuario.getEnabled(), true, true, true,
					authorities);

		} catch (FeignException e) {
			String error = "Error en el login, no existe el usuario '" + username + "' en el sistema";
			log.error(error);
			tracer.currentSpan().tag("error.mensaje", error + ": " + e.getMessage());
			throw new UsernameNotFoundException(error);
		}
	}

	@Override
	public Usuario findByUsername(String username) {
		return client.findByUsername(username);
	}

	@Override
	public Usuario update(Usuario usuario, Long id) {
		return client.update(usuario, id);
	}

}
