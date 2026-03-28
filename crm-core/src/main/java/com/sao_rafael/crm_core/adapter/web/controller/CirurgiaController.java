package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.application.service.CirurgiaService;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.CirurgiaEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cirurgias")
@CrossOrigin(origins = "*")
public class CirurgiaController extends BaseCrudController<CirurgiaEntity, Long, CirurgiaService> {

    public CirurgiaController(CirurgiaService service) {
        super(service);
    }
}