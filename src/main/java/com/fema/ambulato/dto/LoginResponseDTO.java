package com.fema.ambulato.dto;

public class LoginResponseDTO {

    private String token;
    private Long id;
    private String nomeCompleto;
    private String turma;

    public LoginResponseDTO(String token, Long id, String nomeCompleto, String turma) {
        this.token = token;
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.turma = turma;
    }

    public String getToken() { return token; }
    public Long getId() { return id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getTurma() { return turma; }
}
