package com.fema.ambulato.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prontuarios")
public class Prontuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pacienteNome;
    private String celularContato;
    private String tipagemSanguinea;

    private String dnpmAndarQuando;
    private String dnpmFalaQuando;
    private String dnpmDesfraldeQuando;

    @Column(columnDefinition = "TEXT")
    private String subjetivo;

    @Column(columnDefinition = "TEXT")
    private String objetivo;

    @Column(columnDefinition = "TEXT")
    private String avaliacao;

    @Column(columnDefinition = "TEXT")
    private String plano;

    private String nomeAluno;
    private String turma;
    private LocalDateTime dataAtendimento;

    public Prontuario() {
        this.dataAtendimento = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPacienteNome() { return pacienteNome; }
    public void setPacienteNome(String pacienteNome) { this.pacienteNome = pacienteNome; }

    public String getCelularContato() { return celularContato; }
    public void setCelularContato(String celularContato) { this.celularContato = celularContato; }

    public String getTipagemSanguinea() { return tipagemSanguinea; }
    public void setTipagemSanguinea(String tipagemSanguinea) { this.tipagemSanguinea = tipagemSanguinea; }

    public String getDnpmAndarQuando() { return dnpmAndarQuando; }
    public void setDnpmAndarQuando(String dnpmAndarQuando) { this.dnpmAndarQuando = dnpmAndarQuando; }

    public String getDnpmFalaQuando() { return dnpmFalaQuando; }
    public void setDnpmFalaQuando(String dnpmFalaQuando) { this.dnpmFalaQuando = dnpmFalaQuando; }

    public String getDnpmDesfraldeQuando() { return dnpmDesfraldeQuando; }
    public void setDnpmDesfraldeQuando(String dnpmDesfraldeQuando) { this.dnpmDesfraldeQuando = dnpmDesfraldeQuando; }

    public String getSubjetivo() { return subjetivo; }
    public void setSubjetivo(String subjetivo) { this.subjetivo = subjetivo; }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    public String getAvaliacao() { return avaliacao; }
    public void setAvaliacao(String avaliacao) { this.avaliacao = avaliacao; }

    public String getPlano() { return plano; }
    public void setPlano(String plano) { this.plano = plano; }

    public String getNomeAluno() { return nomeAluno; }
    public void setNomeAluno(String nomeAluno) { this.nomeAluno = nomeAluno; }

    public String getTurma() { return turma; }
    public void setTurma(String turma) { this.turma = turma; }

    public LocalDateTime getDataAtendimento() { return dataAtendimento; }
    public void setDataAtendimento(LocalDateTime dataAtendimento) { this.dataAtendimento = dataAtendimento; }
}