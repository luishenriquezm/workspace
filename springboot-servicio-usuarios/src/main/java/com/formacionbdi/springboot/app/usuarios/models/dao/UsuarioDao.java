package com.formacionbdi.springboot.app.usuarios.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.formacionbdi.springboot.app.usuarioscommons.models.entity.Usuario;

@RepositoryRestResource(path = "usuarios")
public interface UsuarioDao extends PagingAndSortingRepository<Usuario, Long> {
	
	@RestResource(path="buscar-username")
	public Usuario findByUsername(@Param("username") String username);
	
	//public Usuario findByUsernameAndEmail(String username, String email);  
	
	@Query("select u from Usuario u where u.username=?1")
	//@Query(value="select u from Usuario u where u.username=?1", nativeQuery = true)   // Para consulta nativa
	public Usuario obtenerPorUsername (String username);

}
