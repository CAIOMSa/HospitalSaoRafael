package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.application.service.MedicoService;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.MedicoEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/medicos")
@CrossOrigin(origins = "*")
public class MedicoController extends BaseCrudController<MedicoEntity, Long, MedicoService> {

    public MedicoController(MedicoService service) {
        super(service);
    }
}