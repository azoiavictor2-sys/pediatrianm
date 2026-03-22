package com.fema.ambulato.controller;

import com.fema.ambulato.model.Prontuario;
import com.fema.ambulato.repository.ProntuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prontuarios")
@CrossOrigin(origins = "*")
public class ProntuarioController {

    @Autowired
    private ProntuarioRepository repository;

    @PostMapping("/salvar")
    public ResponseEntity<String> salvarProntuario(@RequestBody Prontuario novoProntuario) {
        repository.save(novoProntuario);
        return ResponseEntity.ok("Prontuário salvo e gravado com sucesso no Banco de Dados!");
    }

    @GetMapping("/listar/{nomeDoAluno}")
    public ResponseEntity<List<Prontuario>> listarMeusProntuarios(@PathVariable String nomeDoAluno) {
        List<Prontuario> lista = repository.findByNomeAluno(nomeDoAluno);
        return ResponseEntity.ok(lista);
    }

    // NOVA ROTA: Excluir Prontuário
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<String> deletarProntuario(@PathVariable Long id) {
        if(repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok("Prontuário excluído com sucesso.");
        } else {
            return ResponseEntity.status(404).body("Prontuário não encontrado.");
        }
    }
}