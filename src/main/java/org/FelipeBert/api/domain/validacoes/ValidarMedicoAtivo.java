package org.FelipeBert.api.domain.validacoes;

import org.FelipeBert.api.domain.dto.in.CadastrarConsultaDTO;
import org.FelipeBert.api.infra.repository.MedicoRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidarMedicoAtivo implements ValidadorAgendamentoDeConsulta{

    private MedicoRepository medicoRepository;

    public ValidarMedicoAtivo(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    public void validar(CadastrarConsultaDTO dados){
        if(dados.idMedico() == null){
            return;
        }

        boolean medicoEstaAtivo = medicoRepository.findAtivoById(dados.idMedico());

        if(!medicoEstaAtivo){
            throw new IllegalArgumentException("Não é possível agendar consultas com Medicos inativos.");
        }
    }
}
