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

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<String> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        prontuarioService.deletar(id, userDetails.getUsername());
        return ResponseEntity.ok("Prontuário excluído com sucesso.");
    }
}