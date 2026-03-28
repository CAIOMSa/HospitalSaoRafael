package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.CirurgiaEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.CirurgiaJpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CirurgiaService extends BaseCrudService<CirurgiaEntity, Long> {

    public CirurgiaService(CirurgiaJpaRepository repository) {
        super(repository);
    }

    @Override
    protected void copyForUpdate(CirurgiaEntity current, CirurgiaEntity payload) {
        if (payload.getPaciente() == null) {
            throw new IllegalArgumentException("O Paciente é obrigatório para a operação");
        }

        if (payload.getMedico() == null) {
            throw new IllegalArgumentException("O Médico é obrigatório para a operação");
        }

        if (payload.getDataAgendada() == null) {
            throw new IllegalArgumentException("A data agendada é obrigatória.");
        }

        if (payload.getRisco() == CirurgiaEntity.RiscoCirurgia.CRITICO && payload.getSala() == null) {
            throw new IllegalArgumentException("Cirurgias críticas devem possuir sala definida.");
        }

        if (payload.getDataRealizada() != null &&
                !payload.getDataRealizada().toLocalDate().equals(payload.getDataAgendada().toLocalDate())) {
            throw new IllegalArgumentException("A operação deve acontecer no mesmo dia do agendamento.");
        }

        current.setRisco(payload.getRisco());
        current.setAtualizadoEm(LocalDateTime.now());
        current.setSala(payload.getSala());
        current.setStatus(payload.getStatus());
        current.setDataRealizada(LocalDateTime.now());
        current.setPaciente(payload.getPaciente());
        current.setMedico(payload.getMedico());
        current.setDataAgendada(payload.getDataAgendada());

    }

    @Override
    protected String entityName() {
        return "Cirurgia";
    }
}
