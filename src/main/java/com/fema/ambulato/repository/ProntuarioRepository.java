package com.fema.ambulato.repository;

import com.fema.ambulato.model.Prontuario;
import com.fema.ambulato.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {

    List<Prontuario> findByAluno(Usuario aluno);

    List<Prontuario> findByAlunoOrderByDataAtendimentoDesc(Usuario aluno);

    // Busca prontuarios de TODOS os alunos pelo nome do paciente (case insensitive)
    List<Prontuario> findByPacienteNomeContainingIgnoreCaseOrderByDataAtendimentoDesc(String pacienteNome);
}