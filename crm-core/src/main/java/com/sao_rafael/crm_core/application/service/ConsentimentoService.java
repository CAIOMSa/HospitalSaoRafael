package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.ConsentimentoComunicacaoEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.ConsentimentoComunicacaoJpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConsentimentoService extends BaseCrudService<ConsentimentoComunicacaoEntity, Long> {

    public ConsentimentoService(ConsentimentoComunicacaoJpaRepository repository) {
        super(repository);
    }

    @Override
    protected void beforeUpdate(ConsentimentoComunicacaoEntity current, ConsentimentoComunicacaoEntity payload) {
        if (payload.getCanal() == null) {
            throw new IllegalArgumentException("O cliente deverá ser contatado por um canal válido.");
        }

        if (payload.getFinalidade() == null) {
            throw new IllegalArgumentException("Deverá haver uma finalidade para o contato com o cliente.");
        }

        if (payload.getRegistradoPor() == null) {
            throw new IllegalArgumentException("O responsável pelo registro do consentimento é obrigatório.");
        }
    }

    @Override
    protected void copyForUpdate(ConsentimentoComunicacaoEntity current, ConsentimentoComunicacaoEntity payload) {
        current.setPaciente_id(payload.getPaciente_id());
        current.setConcedido(payload.isConcedido());
        current.setCanal(payload.getCanal());
        current.setFinalidade(payload.getFinalidade());
        current.setOrigemAtendimento(payload.getOrigemAtendimento());
        current.setRegistradoPor(payload.getRegistradoPor());
        current.setAtualizadoEm(LocalDateTime.now());
    }

    @Override
    protected String entityName() {
        return "Consentimento";
    }
}
