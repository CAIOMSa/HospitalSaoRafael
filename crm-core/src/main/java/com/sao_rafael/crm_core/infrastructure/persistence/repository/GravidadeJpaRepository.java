package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.GravidadeEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface GravidadeJpaRepository extends BaseCrudRepository <GravidadeEntity, Long>{
}
