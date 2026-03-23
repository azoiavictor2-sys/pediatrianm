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

    /**
     * SALVAR — Sempre cria um prontuário NOVO.
     * O id é forçado como null para impedir que alguém sobrescreva
     * o prontuário de outro aluno enviando um id existente.
     */
    public Prontuario salvar(Prontuario prontuario, String usernameDoAluno) {
        Usuario aluno = usuarioRepository.findByUsername(usernameDoAluno)
                .orElseThrow(() -> new UsernameNotFoundException("Aluno não encontrado."));

        // SEGURANCA: Força criação de novo registro, ignora qualquer id enviado
        prontuario.setId(null);
        prontuario.setAluno(aluno);
        prontuario.setDataAtendimento(LocalDateTime.now());

        return prontuarioRepository.save(prontuario);
    }

    /**
     * LISTAR — Retorna apenas os prontuários do aluno logado.
     */
    @Transactional(readOnly = true)
    public List<Prontuario> listarPorAluno(String usernameDoAluno) {
        Usuario aluno = usuarioRepository.findByUsername(usernameDoAluno)
                .orElseThrow(() -> new UsernameNotFoundException("Aluno não encontrado."));

        return prontuarioRepository.findByAlunoOrderByDataAtendimentoDesc(aluno);
    }

    /**
     * BUSCAR POR ID — Retorna apenas se o prontuário pertence ao aluno logado.
     */
    @Transactional(readOnly = true)
    public Prontuario buscarPorId(Long id, String usernameDoAluno) {
        Prontuario prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Prontuário não encontrado."));

        if (!prontuario.getAluno().getUsername().equals(usernameDoAluno)) {
            throw new SecurityException("Voce nao tem permissao para acessar este prontuario.");
        }

        return prontuario;
    }

    /**
     * ATUALIZAR — Só permite se o prontuário pertence ao aluno logado.
     */
    public Prontuario atualizar(Long id, Prontuario atualizado, String usernameDoAluno) {
        Prontuario existente = prontuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Prontuário não encontrado."));

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

    /**
     * DELETAR — Só permite se o prontuário pertence ao aluno logado.
     */
    public void deletar(Long id, String usernameDoAluno) {
        Prontuario prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Prontuário não encontrado."));

        if (!prontuario.getAluno().getUsername().equals(usernameDoAluno)) {
            throw new SecurityException("Voce nao tem permissao para excluir este prontuario.");
        }

        prontuarioRepository.deleteById(id);
    }

    /**
     * ESTATÍSTICAS — Apenas do aluno logado.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obterEstatisticas(String usernameDoAluno) {
        Usuario aluno = usuarioRepository.findByUsername(usernameDoAluno)
                .orElseThrow(() -> new UsernameNotFoundException("Aluno não encontrado."));

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