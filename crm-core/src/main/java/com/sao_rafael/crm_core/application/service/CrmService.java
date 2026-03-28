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
        validate(crm);
    }

    @Override
    protected void copyForUpdate(CrmEntity current, CrmEntity payload) {
        validate(payload);

        current.setCrm(payload.getCrm());
        current.setUf(payload.getUf());
        current.setMedico(payload.getMedico());
    }

    private void validate(CrmEntity crm) {
        if (crm == null) {
            throw new IllegalArgumentException("CRM payload is required");
        }

        String crmValue = crm.getCrm() == null ? null : crm.getCrm().trim();
        if (crmValue == null || crmValue.isEmpty()) {
            throw new IllegalArgumentException("CRM is required");
        }
        if (!crmValue.matches("\\d+")) {
            throw new IllegalArgumentException("CRM must contain only digits");
        }
        crm.setCrm(crmValue);

        if (crm.getUf() == null) {
            throw new IllegalArgumentException("UF is required");
        }

        if (crm.getMedico() == null || crm.getMedico().getId() == null) {
            throw new IllegalArgumentException("Médico is required");
        }

        if (!medicoRepository.existsById(crm.getMedico().getId())) {
            throw new IllegalArgumentException("Médico not found: " + crm.getMedico().getId());
        }
    }

    @Override
    protected String entityName() {
        return "CRM";
    }
}
