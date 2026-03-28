package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.ConversaEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversaJpaRepository extends BaseCrudRepository<ConversaEntity, Long>{
}
