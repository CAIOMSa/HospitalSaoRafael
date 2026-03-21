package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.FuncionarioEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.CargoJpaRepository;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.FuncionarioJpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FuncionarioService extends BaseCrudService<FuncionarioEntity, Long> {

    private final FuncionarioJpaRepository funcionarioRepository;
    private final CargoJpaRepository cargoRepository;

    public FuncionarioService(FuncionarioJpaRepository funcionarioRepository, CargoJpaRepository cargoRepository) {
        super(funcionarioRepository);
        this.funcionarioRepository = funcionarioRepository;
        this.cargoRepository = cargoRepository;
    }

    @Override
    protected void beforeCreate(FuncionarioEntity funcionario) {
        if (funcionarioRepository.existsByCpf(funcionario.getCpf())) {
            throw new IllegalArgumentException("CPF already exists: " + funcionario.getCpf());
        }
        if (!cargoRepository.existsById(funcionario.getCargoId())) {
            throw new IllegalArgumentException("Cargo not found: " + funcionario.getCargoId());
        }
        if (funcionario.getAtivo() == null) {
            funcionario.setAtivo(true);
        }
        if (funcionario.getCriadoEm() == null) {
            funcionario.setCriadoEm(LocalDateTime.now());
        }
    }

    @Override
    protected void copyForUpdate(FuncionarioEntity current, FuncionarioEntity payload) {
        if (!cargoRepository.existsById(payload.getCargoId())) {
            throw new IllegalArgumentException("Cargo not found: " + payload.getCargoId());
        }
        if (!current.getCpf().equals(payload.getCpf()) && funcionarioRepository.existsByCpf(payload.getCpf())) {
            throw new IllegalArgumentException("CPF already exists: " + payload.getCpf());
        }
        current.setNome(payload.getNome());
        current.setCpf(payload.getCpf());
        current.setCargoId(payload.getCargoId());
        current.setAtivo(payload.getAtivo());
    }

    @Override
    protected String entityName() {
        return "Funcionario";
    }
}
