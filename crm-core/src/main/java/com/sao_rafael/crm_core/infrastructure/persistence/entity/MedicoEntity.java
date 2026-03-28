package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "medicos")
public class MedicoEntity extends FuncionarioEntity{

    @OneToOne
    @JoinColumn(name = "crm_id", nullable = false, unique = true)
    private CrmEntity crm;

    public CrmEntity getCrm() {return crm;}

    public void setCrm(CrmEntity crm) {this.crm = crm;}
}
