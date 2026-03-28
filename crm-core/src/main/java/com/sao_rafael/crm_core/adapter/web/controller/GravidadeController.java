package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.application.service.GravidadeService;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.GravidadeEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/gravidades")
@CrossOrigin(origins = "*")
public class GravidadeController extends BaseCrudController<GravidadeEntity, Long, GravidadeService> {

    public GravidadeController(GravidadeService service) {
        super(service);
    }
}
