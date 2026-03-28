package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.application.service.JornadaPacienteService;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.JornadaPacienteEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/jornadas-paciente")
@CrossOrigin(origins = "*")
public class JornadaPacienteController extends BaseCrudController<JornadaPacienteEntity, Long, JornadaPacienteService> {

    public JornadaPacienteController(JornadaPacienteService service) {
        super(service);
    }
}