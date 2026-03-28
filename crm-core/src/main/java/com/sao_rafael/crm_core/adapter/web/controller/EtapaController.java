package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.application.service.EtapaService;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.EtapaEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/etapas")
@CrossOrigin(origins = "*")
public class EtapaController extends BaseCrudController<EtapaEntity, Long, EtapaService> {

    public EtapaController(EtapaService service) {
        super(service);
    }
}