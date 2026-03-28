package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.application.service.AgendamentoService;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.AgendamentoEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/agendamentos")
@CrossOrigin(origins = "*")
public class AgendamentoController extends BaseCrudController<AgendamentoEntity, Long, AgendamentoService> {

    public AgendamentoController(AgendamentoService service) {
        super(service);
    }
}
