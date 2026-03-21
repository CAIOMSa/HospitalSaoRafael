package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.application.service.FuncionarioService;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.FuncionarioEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/funcionarios")
@CrossOrigin(origins = "*")
public class FuncionarioController extends BaseCrudController<FuncionarioEntity, Long, FuncionarioService> {

    public FuncionarioController(FuncionarioService service) {
        super(service);
    }
}
