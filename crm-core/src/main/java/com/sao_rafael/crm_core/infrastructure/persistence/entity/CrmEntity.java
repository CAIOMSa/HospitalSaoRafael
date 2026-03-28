package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "crms",uniqueConstraints = @UniqueConstraint(columnNames = {"crm", "uf"}))
public class CrmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private MedicoEntity medico;

    @Column(name = "crm", length = 20, nullable = false)
    @Pattern(regexp = "\\d+", message = "CRM deve conter apenas números")
    private String crm;

    @Enumerated(EnumType.STRING)
    @Column(name = "uf", nullable = false)
    private Uf uf;

    public enum Uf{
        AC, AL, AP, AM, BA, CE, DF, ES, GO, MA, MT, MS, MG, PA, PB, PR, PE, PI, RJ, RN, RS, RO, RR, SC, SP, SE, TO
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public MedicoEntity getMedico() {return medico;}

    public void setMedico(MedicoEntity medico) {this.medico = medico;}

    public String getCrm() {return crm;}

    public void setCrm(String crm) {this.crm = crm;}

    public Uf getUf() {return uf;}

    public void setUf(Uf uf) {this.uf = uf;}
}
