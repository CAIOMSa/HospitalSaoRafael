package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.GravidadeEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.GravidadeJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class GravidadeService extends BaseCrudService<GravidadeEntity, Long> {

    public GravidadeService(GravidadeJpaRepository repository) {
        super(repository);
    }

    @Override
    protected void copyForUpdate(GravidadeEntity current, GravidadeEntity payload) {

        current.setNome(payload.getNome());
        current.setDescricao(payload.getDescricao());

    }

    @Override
    protected String entityName() {
        return "Gravidade";
    }
}
