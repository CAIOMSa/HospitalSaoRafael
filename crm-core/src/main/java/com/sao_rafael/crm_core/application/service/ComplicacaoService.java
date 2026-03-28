package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.ComplicacaoCirurgiaEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.ComplicacaoCirurgiaJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ComplicacaoService extends BaseCrudService<ComplicacaoCirurgiaEntity, Long> {

    public ComplicacaoService(ComplicacaoCirurgiaJpaRepository repository) {
        super(repository);
    }

    @Override
    protected void copyForUpdate(ComplicacaoCirurgiaEntity current, ComplicacaoCirurgiaEntity payload) {
        if (payload.getCirurgia() == null) {
            throw new IllegalArgumentException("A cirurgia deve acontecer para ter complicações.");
        }
        current.setDescricao(payload.getDescricao());
        current.setCirurgia(payload.getCirurgia());
    }

    @Override
    protected String entityName() {
        return "Complicação";
    }
}
