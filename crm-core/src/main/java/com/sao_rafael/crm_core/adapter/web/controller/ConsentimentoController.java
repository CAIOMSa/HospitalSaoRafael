package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.application.service.ConsentimentoService;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.ConsentimentoComunicacaoEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/consentimentos")
@CrossOrigin(origins = "*")
public class ConsentimentoController extends BaseCrudController<ConsentimentoComunicacaoEntity, Long, ConsentimentoService> {

    public ConsentimentoController(ConsentimentoService service) {
        super(service);
    }
}