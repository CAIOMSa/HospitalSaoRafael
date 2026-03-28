package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.CirurgiaEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface CirurgiaJpaRepository extends BaseCrudRepository<CirurgiaEntity, Long>{
}
