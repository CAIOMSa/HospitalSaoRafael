package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.MedicoEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicoJpaRepository extends BaseCrudRepository<MedicoEntity, Long>{
}
