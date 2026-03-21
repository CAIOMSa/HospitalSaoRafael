package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.application.service.CargoService;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.CargoEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cargos")
@CrossOrigin(origins = "*")
public class CargoController extends BaseCrudController<CargoEntity, Long, CargoService> {

    public CargoController(CargoService service) {
        super(service);
    }
}
