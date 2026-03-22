package com.fema.ambulato.repository;

import com.fema.ambulato.model.Prontuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {

    // Busca antiga (caso precise depois)
    List<Prontuario> findByNomeAlunoAndTurma(String nomeAluno, String turma);

    // Busca nova (que o nosso HTML vai usar agora)
    List<Prontuario> findByNomeAluno(String nomeAluno);
}