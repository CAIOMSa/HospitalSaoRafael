package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.MensagemEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.MensagemJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class MensagemService extends BaseCrudService<MensagemEntity, Long> {

    public MensagemService(MensagemJpaRepository repository) {
        super(repository);
    }

    @Override
    protected void copyForUpdate(MensagemEntity current, MensagemEntity payload) {

        current.setConversa(payload.getConversa());
        current.setEnviadoPor(payload.getEnviadoPor());
        current.setConteudo(payload.getConteudo());

    }

    @Override
    protected String entityName() {
        return "Mensagem";
    }
}
