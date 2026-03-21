package com.sao_rafael.crm_core.application.service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public abstract class BaseCrudService<E, ID> {

    protected final org.springframework.data.jpa.repository.JpaRepository<E, ID> repository;

    protected BaseCrudService(org.springframework.data.jpa.repository.JpaRepository<E, ID> repository) {
        this.repository = repository;
    }

    public E create(E entity) {
        beforeCreate(entity);
        return repository.save(entity);
    }

    @Transactional(readOnly = true)
    public E getById(ID id) {
        return repository.findById(id).orElseThrow(() -> notFound(id));
    }

    @Transactional(readOnly = true)
    public List<E> getAll() {
        return repository.findAll();
    }

    public E update(ID id, E payload) {
        E current = getById(id);
        copyForUpdate(current, payload);
        beforeUpdate(current, payload);
        return repository.save(current);
    }

    protected void beforeCreate(E entity) {
    }

    protected void beforeUpdate(E current, E payload) {
    }

    protected RuntimeException notFound(ID id) {
        return new IllegalArgumentException(entityName() + " not found: " + id);
    }

    protected String entityName() {
        return "Entity";
    }

    protected abstract void copyForUpdate(E current, E payload);
}
