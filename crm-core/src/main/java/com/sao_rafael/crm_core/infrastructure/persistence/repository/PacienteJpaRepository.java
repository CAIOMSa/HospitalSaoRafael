package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.PacienteEntity;


@Repository
public interface PacienteJpaRepository extends BaseCrudRepository<PacienteEntity, Long> {
}
