package com.fema.ambulato.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "prontuarios")
public class Prontuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do paciente é obrigatório")
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

    @Column(columnDefinition = "TEXT")
    private String dadosGeraisJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    @JsonIgnoreProperties({"senha", "hibernateLazyInitializer", "handler"})
    private Usuario aluno;

    private LocalDateTime dataAtendimento;

    public Prontuario() {
        this.dataAtendimento = LocalDateTime.now();
    }

    // --- GETTERS E SETTERS ---

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

    public String getDadosGeraisJson() { return dadosGeraisJson; }
    public void setDadosGeraisJson(String dadosGeraisJson) { this.dadosGeraisJson = dadosGeraisJson; }

    public Usuario getAluno() { return aluno; }
    public void setAluno(Usuario aluno) { this.aluno = aluno; }

    public LocalDateTime getDataAtendimento() { return dataAtendimento; }
    public void setDataAtendimento(LocalDateTime dataAtendimento) { this.dataAtendimento = dataAtendimento; }
}