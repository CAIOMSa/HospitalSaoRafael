package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.CrmEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface CrmJpaRepository extends BaseCrudRepository<CrmEntity, Long> {
}
