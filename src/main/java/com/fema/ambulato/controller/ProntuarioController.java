package com.fema.ambulato.controller;

import com.fema.ambulato.model.Prontuario;
import com.fema.ambulato.service.ProntuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prontuarios")
public class ProntuarioController {

    @Autowired
    private ProntuarioService prontuarioService;

    @PostMapping("/salvar")
    public ResponseEntity<Prontuario> salvar(
            @Valid @RequestBody Prontuario prontuario,
            @AuthenticationPrincipal UserDetails userDetails) {
        Prontuario salvo = prontuarioService.salvar(prontuario, userDetails.getUsername());
        return ResponseEntity.ok(salvo);
    }

    @GetMapping("/meus")
    public ResponseEntity<List<Prontuario>> listarMeus(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<Prontuario> lista = prontuarioService.listarPorAluno(userDetails.getUsername());
        return ResponseEntity.ok(lista);
    }

    /**
     * Busca prontuarios de TODOS os alunos pelo nome do paciente.
     * Retorna flag "proprietario" para indicar se pode editar.
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Map<String, Object>>> buscarPorPaciente(
            @RequestParam String nome,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<Map<String, Object>> resultados = prontuarioService.buscarPorPaciente(nome, userDetails.getUsername());
        return ResponseEntity.ok(resultados);
    }

    /**
     * Busca prontuario por ID (qualquer usuario pode ver, mas com flag de permissao)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> resultado = prontuarioService.buscarPorIdComPermissao(id, userDetails.getUsername());
        return ResponseEntity.ok(resultado);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<String> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        prontuarioService.deletar(id, userDetails.getUsername());
        return ResponseEntity.ok("Prontuario excluido com sucesso.");
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Prontuario> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Prontuario prontuario,
            @AuthenticationPrincipal UserDetails userDetails) {
        Prontuario atualizado = prontuarioService.atualizar(id, prontuario, userDetails.getUsername());
        return ResponseEntity.ok(atualizado);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> obterEstatisticas(
            @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> stats = prontuarioService.obterEstatisticas(userDetails.getUsername());
        return ResponseEntity.ok(stats);
    }
}