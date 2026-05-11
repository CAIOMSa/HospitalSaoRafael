package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.MensagemEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MensagemJpaRepository extends BaseCrudRepository<MensagemEntity, Long>{
	List<MensagemEntity> findByConversa_IdOrderByDataEnvioAsc(Long conversaId);

	Optional<MensagemEntity> findFirstByConversa_IdOrderByDataEnvioDesc(Long conversaId);
}
