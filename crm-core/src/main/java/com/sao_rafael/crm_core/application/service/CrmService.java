package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.CrmEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.CrmJpaRepository;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.MedicoJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class CrmService extends BaseCrudService<CrmEntity, Long> {

    private final MedicoJpaRepository medicoRepository;

    public CrmService(CrmJpaRepository repository, MedicoJpaRepository medicoRepository) {
        super(repository);
        this.medicoRepository = medicoRepository;
    }

    @Override
    protected void beforeCreate(CrmEntity crm) {
        if (crm.getMedico() == null || crm.getMedico().getId() == null) {
            throw new IllegalArgumentException("Médico is required");
        }

        if (!medicoRepository.existsById(crm.getMedico().getId())) {
            throw new IllegalArgumentException("Médico not found: " + crm.getMedico().getId());
        }
    }

    @Override
    protected void copyForUpdate(CrmEntity current, CrmEntity payload) {
        if (payload.getMedico() == null || payload.getMedico().getId() == null) {
            throw new IllegalArgumentException("Médico is required");
        }

        if (!medicoRepository.existsById(payload.getMedico().getId())) {
            throw new IllegalArgumentException("Médico not found: " + payload.getMedico().getId());
        }

        current.setCrm(payload.getCrm());
        current.setUf(payload.getUf());
        current.setMedico(payload.getMedico());
    }

    @Override
    protected String entityName() {
        return "CRM";
    }
}
