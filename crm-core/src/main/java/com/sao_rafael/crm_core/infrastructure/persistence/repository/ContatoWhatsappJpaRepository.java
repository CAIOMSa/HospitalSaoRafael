package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.ContatoWhatsappEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ContatoWhatsappJpaRepository extends BaseCrudRepository<ContatoWhatsappEntity, Long>{
}
