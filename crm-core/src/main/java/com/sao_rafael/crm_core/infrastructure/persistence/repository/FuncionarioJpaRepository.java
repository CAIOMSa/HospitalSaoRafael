package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.FuncionarioEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface FuncionarioJpaRepository extends BaseCrudRepository<FuncionarioEntity, Long> {
    boolean existsByCpf(String cpf);
}
