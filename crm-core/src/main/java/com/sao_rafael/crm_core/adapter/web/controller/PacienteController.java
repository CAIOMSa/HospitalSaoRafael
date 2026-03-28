package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.application.service.PacienteService;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.PacienteEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pacientes")
@CrossOrigin(origins = "*")
public class PacienteController extends BaseCrudController<PacienteEntity, Long, PacienteService> {

    public PacienteController(PacienteService service) {
        super(service);
    }
}
