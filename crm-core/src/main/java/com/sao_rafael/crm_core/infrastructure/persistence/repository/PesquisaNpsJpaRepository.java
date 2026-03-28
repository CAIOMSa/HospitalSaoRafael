package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.PesquisaNPSEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface PesquisaNpsJpaRepository extends BaseCrudRepository<PesquisaNPSEntity, Long>{
}
