package com.fema.ambulato.service;

import com.fema.ambulato.model.Prontuario;
import com.fema.ambulato.model.Usuario;
import com.fema.ambulato.repository.ProntuarioRepository;
import com.fema.ambulato.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class ProntuarioService {

    @Autowired
    private ProntuarioRepository prontuarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Prontuario salvar(Prontuario prontuario, String usernameDoAluno) {
        Usuario aluno = usuarioRepository.findByUsername(usernameDoAluno)
                .orElseThrow(() -> new UsernameNotFoundException("Aluno nao encontrado."));

        prontuario.setId(null);
        prontuario.setAluno(aluno);
        prontuario.setDataAtendimento(LocalDateTime.now());

        return prontuarioRepository.save(prontuario);
    }

    @Transactional(readOnly = true)
    public List<Prontuario> listarPorAluno(String usernameDoAluno) {
        Usuario aluno = usuarioRepository.findByUsername(usernameDoAluno)
                .orElseThrow(() -> new UsernameNotFoundException("Aluno nao encontrado."));

        return prontuarioRepository.findByAlunoOrderByDataAtendimentoDesc(aluno);
    }

    /**
     * Busca prontuarios de TODOS os alunos pelo nome do paciente.
     * Retorna os prontuarios com uma flag indicando se pertence ao aluno logado.
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> buscarPorPaciente(String nomePaciente, String usernameDoAluno) {
        List<Prontuario> prontuarios = prontuarioRepository
                .findByPacienteNomeContainingIgnoreCaseOrderByDataAtendimentoDesc(nomePaciente);

        return prontuarios.stream().map(p -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", p.getId());
            dto.put("pacienteNome", p.getPacienteNome());
            dto.put("celularContato", p.getCelularContato());
            dto.put("tipagemSanguinea", p.getTipagemSanguinea());
            dto.put("subjetivo", p.getSubjetivo());
            dto.put("objetivo", p.getObjetivo());
            dto.put("avaliacao", p.getAvaliacao());
            dto.put("plano", p.getPlano());
            dto.put("dadosGeraisJson", p.getDadosGeraisJson());
            dto.put("dataAtendimento", p.getDataAtendimento());
            dto.put("dataAtualizacao", p.getDataAtualizacao());
            dto.put("alunoNome", p.getAluno().getNomeCompleto());
            dto.put("alunoId", p.getAluno().getId());
            // Flag: true se o prontuario pertence ao aluno logado
            dto.put("proprietario", p.getAluno().getUsername().equals(usernameDoAluno));
            return dto;
        }).toList();
    }

    @Transactional(readOnly = true)
    public Prontuario buscarPorId(Long id, String usernameDoAluno) {
        Prontuario prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Prontuario nao encontrado."));

        return prontuario;
    }

    /**
     * Buscar por ID para leitura (qualquer usuario pode ver)
     */
    @Transactional(readOnly = true)
    public Map<String, Object> buscarPorIdComPermissao(Long id, String usernameDoAluno) {
        Prontuario p = prontuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Prontuario nao encontrado."));

        Map<String, Object> dto = new HashMap<>();
        dto.put("id", p.getId());
        dto.put("pacienteNome", p.getPacienteNome());
        dto.put("celularContato", p.getCelularContato());
        dto.put("tipagemSanguinea", p.getTipagemSanguinea());
        dto.put("subjetivo", p.getSubjetivo());
        dto.put("objetivo", p.getObjetivo());
        dto.put("avaliacao", p.getAvaliacao());
        dto.put("plano", p.getPlano());
        dto.put("dadosGeraisJson", p.getDadosGeraisJson());
        dto.put("dataAtendimento", p.getDataAtendimento());
        dto.put("dataAtualizacao", p.getDataAtualizacao());
        dto.put("alunoNome", p.getAluno().getNomeCompleto());
        dto.put("alunoId", p.getAluno().getId());
        dto.put("proprietario", p.getAluno().getUsername().equals(usernameDoAluno));
        return dto;
    }

    public Prontuario atualizar(Long id, Prontuario atualizado, String usernameDoAluno) {
        Prontuario existente = prontuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Prontuario nao encontrado."));

        if (!existente.getAluno().getUsername().equals(usernameDoAluno)) {
            throw new SecurityException("Voce nao tem permissao para editar este prontuario.");
        }

        existente.setPacienteNome(atualizado.getPacienteNome());
        existente.setCelularContato(atualizado.getCelularContato());
        existente.setTipagemSanguinea(atualizado.getTipagemSanguinea());
        existente.setSubjetivo(atualizado.getSubjetivo());
        existente.setObjetivo(atualizado.getObjetivo());
        existente.setAvaliacao(atualizado.getAvaliacao());
        existente.setPlano(atualizado.getPlano());
        existente.setDadosGeraisJson(atualizado.getDadosGeraisJson());
        existente.setDataAtualizacao(LocalDateTime.now());

        return prontuarioRepository.save(existente);
    }

    public void deletar(Long id, String usernameDoAluno) {
        Prontuario prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Prontuario nao encontrado."));

        if (!prontuario.getAluno().getUsername().equals(usernameDoAluno)) {
            throw new SecurityException("Voce nao tem permissao para excluir este prontuario.");
        }

        prontuarioRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obterEstatisticas(String usernameDoAluno) {
        Usuario aluno = usuarioRepository.findByUsername(usernameDoAluno)
                .orElseThrow(() -> new UsernameNotFoundException("Aluno nao encontrado."));

        List<Prontuario> todos = prontuarioRepository.findByAlunoOrderByDataAtendimentoDesc(aluno);

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", todos.size());
        if (!todos.isEmpty()) {
            stats.put("ultimoPaciente", todos.get(0).getPacienteNome());
            stats.put("ultimaData", todos.get(0).getDataAtendimento());
        } else {
            stats.put("ultimoPaciente", null);
            stats.put("ultimaData", null);
        }
        return stats;
    }
}