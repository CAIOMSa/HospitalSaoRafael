package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.StatusEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.StatusJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class StatusService extends BaseCrudService<StatusEntity, Long> {

    public StatusService(StatusJpaRepository repository) {
        super(repository);
    }

    @Override
    protected void copyForUpdate(StatusEntity current, StatusEntity payload) {

        current.setCategoria(payload.getCategoria());
        current.setNome(payload.getNome());
        current.setDescricao(payload.getDescricao());
    }

    @Override
    protected String entityName() {
        return "Status";
    }
}
