package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.StatusEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusJpaRepository extends BaseCrudRepository<StatusEntity, Long>{
}
