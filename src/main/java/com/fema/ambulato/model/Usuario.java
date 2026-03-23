package com.fema.ambulato.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username (CPF) e obrigatorio")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 digitos numericos")
    @Column(unique = true, nullable = false, length = 11)
    private String username;

    @NotBlank(message = "Senha e obrigatoria")
    @Size(max = 200, message = "Senha excedeu o limite")
    @Column(nullable = false)
    private String senha;

    @NotBlank(message = "Nome completo e obrigatorio")
    @Size(max = 200, message = "Nome deve ter no maximo 200 caracteres")
    private String nomeCompleto;

    @NotBlank(message = "Turma e obrigatoria")
    @Size(max = 20, message = "Turma deve ter no maximo 20 caracteres")
    private String turma;

    /** Token JWT ativo — apenas um dispositivo por vez */
    @Column(columnDefinition = "TEXT")
    private String tokenAtivo;

    // --- GETTERS E SETTERS ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getTurma() {
        return turma;
    }

    public void setTurma(String turma) {
        this.turma = turma;
    }

    public String getTokenAtivo() {
        return tokenAtivo;
    }

    public void setTokenAtivo(String tokenAtivo) {
        this.tokenAtivo = tokenAtivo;
    }
}