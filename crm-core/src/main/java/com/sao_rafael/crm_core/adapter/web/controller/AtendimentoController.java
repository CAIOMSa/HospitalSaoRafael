package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.application.service.AtendimentoService;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.AtendimentoEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/atendimentos")
@CrossOrigin(origins = "*")
public class AtendimentoController extends BaseCrudController<AtendimentoEntity, Long, AtendimentoService> {

    public AtendimentoController(AtendimentoService service) {
        super(service);
    }
}
