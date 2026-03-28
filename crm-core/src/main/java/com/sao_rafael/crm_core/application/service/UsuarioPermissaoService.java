package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.UsuarioPermissaoEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.UsuarioPermissaoJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioPermissaoService extends BaseCrudService<UsuarioPermissaoEntity, Long> {

    public UsuarioPermissaoService(UsuarioPermissaoJpaRepository repository) {
        super(repository);
    }

    @Override
    protected void copyForUpdate(UsuarioPermissaoEntity current, UsuarioPermissaoEntity payload) {

        current.setUsuario(payload.getUsuario());
        current.setPermissao(payload.getPermissao());

    }

    @Override
    protected String entityName() {
        return "Usuário Permissão";
    }
}
