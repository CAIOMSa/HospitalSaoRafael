package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.MensagemEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface MensagemJpaRepository extends BaseCrudRepository<MensagemEntity, Long>{
}
