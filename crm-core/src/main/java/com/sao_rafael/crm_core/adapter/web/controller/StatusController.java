package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.application.service.StatusService;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.StatusEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/status")
@CrossOrigin(origins = "*")
public class StatusController extends BaseCrudController<StatusEntity, Long, StatusService> {

    public StatusController(StatusService service) {
        super(service);
    }
}