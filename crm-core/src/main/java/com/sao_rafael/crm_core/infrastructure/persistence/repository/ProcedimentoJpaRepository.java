package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.ProcedimentoEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcedimentoJpaRepository extends BaseCrudRepository<ProcedimentoEntity, Long>{
}
