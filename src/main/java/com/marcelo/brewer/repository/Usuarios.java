package com.marcelo.brewer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marcelo.brewer.model.Usuario;
import com.marcelo.brewer.repository.helper.usuario.UsuariosQueries;

@Repository
public interface Usuarios extends JpaRepository<Usuario, Long>,UsuariosQueries {

	public Optional<Usuario> findByEmail(String nome);
	
	public List<Usuario> findByCodigoIn(Long[] codigos);

}
