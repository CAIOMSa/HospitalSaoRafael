package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.application.service.ProcedimentoService;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.ProcedimentoEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/procedimentos")
@CrossOrigin(origins = "*")
public class ProcedimentoController extends BaseCrudController<ProcedimentoEntity, Long, ProcedimentoService> {

    public ProcedimentoController(ProcedimentoService service) {
        super(service);
    }
}
