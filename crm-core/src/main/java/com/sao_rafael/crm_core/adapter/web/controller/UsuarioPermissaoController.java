package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.application.service.UsuarioPermissaoService;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.UsuarioPermissaoEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios-permissoes")
@CrossOrigin(origins = "*")
public class UsuarioPermissaoController extends BaseCrudController<UsuarioPermissaoEntity, Long, UsuarioPermissaoService> {

    public UsuarioPermissaoController(UsuarioPermissaoService service) {
        super(service);
    }
}