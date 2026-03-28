package com.sao_rafael.crm_core.infrastructure.persistence.repository;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.ComplicacaoCirurgiaEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplicacaoCirurgiaJpaRepository extends BaseCrudRepository<ComplicacaoCirurgiaEntity, Long>{

}
