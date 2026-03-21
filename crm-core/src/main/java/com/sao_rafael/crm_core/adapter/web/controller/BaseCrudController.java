package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.application.service.BaseCrudService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public abstract class BaseCrudController<E, ID, S extends BaseCrudService<E, ID>> {

    protected final S service;

    protected BaseCrudController(S service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<E> create(@RequestBody E payload) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(payload));
    }

    @GetMapping("/{id}")
    public ResponseEntity<E> getById(@PathVariable ID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<E>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<E> update(@PathVariable ID id, @RequestBody E payload) {
        return ResponseEntity.ok(service.update(id, payload));
    }
}
