package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.EtapaEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface EtapaJpaRepository extends BaseCrudRepository<EtapaEntity,Long>{
}
