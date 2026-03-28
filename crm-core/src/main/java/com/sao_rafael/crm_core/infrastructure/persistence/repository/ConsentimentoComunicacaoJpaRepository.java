package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.ConsentimentoComunicacaoEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsentimentoComunicacaoJpaRepository extends BaseCrudRepository<ConsentimentoComunicacaoEntity, Long>{
}
