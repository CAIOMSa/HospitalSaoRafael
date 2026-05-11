package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.ContatoWhatsappEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContatoWhatsappJpaRepository extends BaseCrudRepository<ContatoWhatsappEntity, Long>{
	Optional<ContatoWhatsappEntity> findFirstByNumero(String numero);
}
