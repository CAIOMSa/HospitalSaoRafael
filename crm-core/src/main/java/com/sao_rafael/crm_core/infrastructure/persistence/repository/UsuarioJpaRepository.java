package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioJpaRepository extends BaseCrudRepository<UsuarioEntity, Long> {
    boolean existsByEmail(String email);

    Optional<UsuarioEntity> findFirstByEmailIgnoreCase(String email);

    Optional<UsuarioEntity> findFirstByAtivoTrueOrderByIdAsc();
}
