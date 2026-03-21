package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.application.service.CrmService;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.CrmEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/crms")
@CrossOrigin(origins = "*")
public class CrmController extends BaseCrudController<CrmEntity, Long, CrmService> {

    public CrmController(CrmService service) {
        super(service);
    }
}
