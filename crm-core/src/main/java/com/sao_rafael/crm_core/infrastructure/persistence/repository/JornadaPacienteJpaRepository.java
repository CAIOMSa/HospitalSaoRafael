package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.JornadaPacienteEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface JornadaPacienteJpaRepository extends BaseCrudRepository<JornadaPacienteEntity, Long>{
}
