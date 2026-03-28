package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.application.service.EnderecoService;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.EnderecoEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/enderecos")
@CrossOrigin(origins = "*")
public class EnderecoController extends BaseCrudController<EnderecoEntity, Long, EnderecoService> {

    public EnderecoController(EnderecoService service) {
        super(service);
    }
}