package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.application.service.InternacaoService;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.InternacaoEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internacoes")
@CrossOrigin(origins = "*")
public class InternacaoController extends BaseCrudController<InternacaoEntity, Long, InternacaoService> {

    public InternacaoController(InternacaoService service) {
        super(service);
    }
}
