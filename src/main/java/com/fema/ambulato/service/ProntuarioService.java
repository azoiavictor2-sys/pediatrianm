package com.fema.ambulato.service;

import com.fema.ambulato.model.Prontuario;
import com.fema.ambulato.model.Usuario;
import com.fema.ambulato.repository.ProntuarioRepository;
import com.fema.ambulato.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProntuarioService {

    @Autowired
    private ProntuarioRepository prontuarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Prontuario salvar(Prontuario prontuario, String usernameDoAluno) {
        Usuario aluno = usuarioRepository.findByUsername(usernameDoAluno)
                .orElseThrow(() -> new UsernameNotFoundException("Aluno não encontrado."));

        prontuario.setAluno(aluno);
        return prontuarioRepository.save(prontuario);
    }

    public List<Prontuario> listarPorAluno(String usernameDoAluno) {
        Usuario aluno = usuarioRepository.findByUsername(usernameDoAluno)
                .orElseThrow(() -> new UsernameNotFoundException("Aluno não encontrado."));

        return prontuarioRepository.findByAluno(aluno);
    }

    public void deletar(Long id, String usernameDoAluno) {
        Prontuario prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Prontuário não encontrado."));

        // Garante que o aluno só pode deletar seus próprios prontuários
        if (!prontuario.getAluno().getUsername().equals(usernameDoAluno)) {
            throw new SecurityException("Você não tem permissão para excluir este prontuário.");
        }

        prontuarioRepository.deleteById(id);
    }
}
