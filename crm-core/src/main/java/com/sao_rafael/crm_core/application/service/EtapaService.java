package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.EtapaEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.EtapaJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class EtapaService extends BaseCrudService<EtapaEntity, Long> {

    public EtapaService(EtapaJpaRepository repository) {
        super(repository);
    }

    @Override
    protected void copyForUpdate(EtapaEntity current, EtapaEntity payload) {

    current.setNome(payload.getNome());
    }

    @Override
    protected String entityName() {
        return "Etapa";
    }
}