package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.AtendimentoEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface AtendimentoJpaRepository extends BaseCrudRepository<AtendimentoEntity, Long>{
}
