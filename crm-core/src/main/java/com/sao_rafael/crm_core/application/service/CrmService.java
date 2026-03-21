package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.CrmEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.CrmJpaRepository;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.FuncionarioJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class CrmService extends BaseCrudService<CrmEntity, Long> {

    private final FuncionarioJpaRepository funcionarioRepository;

    public CrmService(CrmJpaRepository repository, FuncionarioJpaRepository funcionarioRepository) {
        super(repository);
        this.funcionarioRepository = funcionarioRepository;
    }

    @Override
    protected void beforeCreate(CrmEntity crm) {
        if (!funcionarioRepository.existsById(crm.getIdFuncionario())) {
            throw new IllegalArgumentException("Funcionario not found: " + crm.getIdFuncionario());
        }
    }

    @Override
    protected void copyForUpdate(CrmEntity current, CrmEntity payload) {
        if (!funcionarioRepository.existsById(payload.getIdFuncionario())) {
            throw new IllegalArgumentException("Funcionario not found: " + payload.getIdFuncionario());
        }
        current.setCrm(payload.getCrm());
        current.setIdFuncionario(payload.getIdFuncionario());
    }

    @Override
    protected String entityName() {
        return "CRM";
    }
}
