package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.EnderecoEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface EnderecoJpaRepository extends BaseCrudRepository<EnderecoEntity, Long>{
}
