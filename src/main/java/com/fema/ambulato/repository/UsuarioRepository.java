package com.fema.ambulato.repository;

import com.fema.ambulato.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Busca no banco se existe alguém com esse usuário E com essa senha exata
    Optional<Usuario> findByUsernameAndSenha(String username, String senha);
}