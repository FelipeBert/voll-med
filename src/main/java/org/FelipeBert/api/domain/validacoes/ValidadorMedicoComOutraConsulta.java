package org.FelipeBert.api.domain.validacoes;

import org.FelipeBert.api.domain.dto.in.CadastrarConsultaDTO;
import org.FelipeBert.api.infra.repository.ConsultaRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidadorMedicoComOutraConsulta implements ValidadorAgendamentoDeConsulta{

    private ConsultaRepository consultaRepository;

    public ValidadorMedicoComOutraConsulta(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    public void validar(CadastrarConsultaDTO dados){
        boolean medicoOcupado = consultaRepository.existsByMedicoIdAndDataAndHora(dados.idMedico(), dados.hora().toLocalDate(),  dados.hora().toLocalTime());
        if (medicoOcupado) {
            throw new IllegalArgumentException("O médico já possui outra consulta agendada para este horário.");
        }
    }
}
