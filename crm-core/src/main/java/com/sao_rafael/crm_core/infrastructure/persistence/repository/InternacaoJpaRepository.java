package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.InternacaoEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface InternacaoJpaRepository extends BaseCrudRepository<InternacaoEntity, Long>{
}
