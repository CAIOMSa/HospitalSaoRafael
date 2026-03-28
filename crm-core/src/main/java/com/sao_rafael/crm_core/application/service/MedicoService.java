package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.MedicoEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.MedicoJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class MedicoService extends BaseCrudService<MedicoEntity, Long> {

    public MedicoService(MedicoJpaRepository repository) {
        super(repository);
    }

    @Override
    protected void copyForUpdate(MedicoEntity current, MedicoEntity payload) {
        if (payload.getCrm() == null) {
            throw new IllegalArgumentException("Todo médico deverá ter um CRM");
        }

        current.setCrm(payload.getCrm());
    }

    @Override
    protected String entityName() {
        return "Médico";
    }
}
