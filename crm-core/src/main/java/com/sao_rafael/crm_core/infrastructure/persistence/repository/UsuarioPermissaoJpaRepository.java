package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.UsuarioPermissaoEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioPermissaoJpaRepository extends BaseCrudRepository<UsuarioPermissaoEntity, Long>{

}
