package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "internacao")
public class InternacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private PacienteEntity paciente;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private StatusEntity status;

    @Column(name = "data_entrada", nullable = false)
    private LocalDateTime dataEntrada;

    @Column(name = "data_saida", nullable = false)
    private LocalDateTime dataSaida;

    @Column(name = "leito", length = 20, nullable = false)
    private String leito;

    @Column(name = "motivo", length = 255, nullable = false)
    private String motivo;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public PacienteEntity getPaciente() {return paciente;}

    public void setPaciente(PacienteEntity paciente) {this.paciente = paciente;}

    public StatusEntity getStatus() {return status;}

    public void setStatus(StatusEntity status) {this.status = status;}

    public LocalDateTime getDataEntrada() {return dataEntrada;}

    public void setDataEntrada(LocalDateTime dataEntrada) {this.dataEntrada = dataEntrada;}

    public LocalDateTime getDataSaida() {return dataSaida;}

    public void setDataSaida(LocalDateTime dataSaida) {this.dataSaida = dataSaida;}

    public String getLeito() {return leito;}

    public void setLeito(String leito) {this.leito = leito;}

    public String getMotivo() {return motivo;}

    public void setMotivo(String motivo) {this.motivo = motivo;}
}
