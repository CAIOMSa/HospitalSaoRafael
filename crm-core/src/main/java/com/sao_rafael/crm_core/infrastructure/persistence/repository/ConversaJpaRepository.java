package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.ConversaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversaJpaRepository extends BaseCrudRepository<ConversaEntity, Long>{
	List<ConversaEntity> findAllByOrderByAtualizadoEmDesc();

	Optional<ConversaEntity> findFirstByContato_IdOrderByAtualizadoEmDesc(Long contatoId);
}
