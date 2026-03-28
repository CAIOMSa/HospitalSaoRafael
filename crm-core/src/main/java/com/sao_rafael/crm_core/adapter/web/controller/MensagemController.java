package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.application.service.MensagemService;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.MensagemEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mensagens")
@CrossOrigin(origins = "*")
public class MensagemController extends BaseCrudController<MensagemEntity, Long, MensagemService> {

    public MensagemController(MensagemService service) {
        super(service);
    }
}
