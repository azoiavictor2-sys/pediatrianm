package com.fema.ambulato.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class LoginRequestDTO {

    @NotBlank(message = "Username (CPF) e obrigatorio")
    private String username;

    @NotBlank(message = "Senha e obrigatoria")
    @Size(min = 4, max = 100, message = "Senha deve ter entre 4 e 100 caracteres")
    private String senha;

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
}