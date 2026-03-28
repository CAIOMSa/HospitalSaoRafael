package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.PermissaoEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissaoJpaRepository extends BaseCrudRepository<PermissaoEntity, Long>{
}
