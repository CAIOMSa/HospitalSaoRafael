package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.EnderecoEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.EnderecoJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class EnderecoService extends BaseCrudService<EnderecoEntity, Long> {

    public EnderecoService(EnderecoJpaRepository repository) {
        super(repository);
    }

    @Override
    protected void copyForUpdate(EnderecoEntity current, EnderecoEntity payload) {

        if (payload.getRua() == null) {
            throw new IllegalArgumentException("O cliente deverá ter um nome de rua valido");
        }
        if (payload.getNumero() == null) {
            throw new IllegalArgumentException("O cliente deverá ter um número de residência valido");
        }
        if (payload.getBairro() == null) {
            throw new IllegalArgumentException("O cliente deverá residir em um bairro");
        }
        if (payload.getCidade() == null) {
            throw new IllegalArgumentException("O cliente deverá residir em uma cidade");
        }
        if (payload.getCep() == null) {
            throw new IllegalArgumentException("A residência do cliente deverá ter um número válido");
        }
        if (payload.getEstado() == null) {
            throw new IllegalArgumentException("O Estado deverá ser valido");
        }

        current.setRua(payload.getRua());
        current.setNumero(payload.getNumero());
        current.setBairro(payload.getBairro());
        current.setCidade(payload.getCidade());
        current.setCep(payload.getCep());
        current.setEstado(payload.getEstado());
        current.setComplemento(payload.getComplemento());

    }

    @Override
    protected String entityName() {
        return "Endereço";
    }
}