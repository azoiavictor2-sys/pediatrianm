package com.fema.ambulato.repository;

import com.fema.ambulato.model.Prontuario;
import com.fema.ambulato.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {

    // Busca prontuários pelo objeto aluno (relação ManyToOne)
    List<Prontuario> findByAluno(Usuario aluno);
}