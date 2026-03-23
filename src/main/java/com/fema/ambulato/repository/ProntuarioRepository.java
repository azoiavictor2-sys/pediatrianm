package com.fema.ambulato.repository;

import com.fema.ambulato.model.Prontuario;
import com.fema.ambulato.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {

    List<Prontuario> findByAluno(Usuario aluno);

    // Busca prontuários do aluno ordenados do mais recente ao mais antigo
    List<Prontuario> findByAlunoOrderByDataAtendimentoDesc(Usuario aluno);
}