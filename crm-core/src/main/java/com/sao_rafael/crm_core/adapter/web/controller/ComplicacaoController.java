package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.application.service.ComplicacaoService;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.ComplicacaoCirurgiaEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/complicacoes")
@CrossOrigin(origins = "*")
public class ComplicacaoController extends BaseCrudController<ComplicacaoCirurgiaEntity, Long, ComplicacaoService> {

    public ComplicacaoController(ComplicacaoService service) {
        super(service);
    }
}