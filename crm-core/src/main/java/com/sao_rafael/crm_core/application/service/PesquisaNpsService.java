package com.sao_rafael.crm_core.application.service;


import com.sao_rafael.crm_core.infrastructure.persistence.entity.PesquisaNPSEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.PesquisaNpsJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class PesquisaNpsService extends BaseCrudService<PesquisaNPSEntity, Long> {


    public PesquisaNpsService(PesquisaNpsJpaRepository repository){
        super(repository);
    }

    @Override
    protected void copyForUpdate(PesquisaNPSEntity current, PesquisaNPSEntity payload){
        current.setNota(payload.getNota());
        current.setAtendimento(payload.getAtendimento());
        current.setPaciente(payload.getPaciente());
    }

    protected void beforeCreate(PesquisaNPSEntity nps){
        if(nps.getNota() < 0 || nps.getNota() > 10){
            throw new IllegalArgumentException("Nota NPS deve ser entre 0 e 10");
        }
    }

    public String classificarNPS(int nota) {
        if (nota <= 6) return "DETRATOR";
        if (nota <= 8) return "NEUTRO";
        return "PROMOTOR";
    }
}
