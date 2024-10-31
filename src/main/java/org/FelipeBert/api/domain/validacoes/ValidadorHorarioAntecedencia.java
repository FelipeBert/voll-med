package org.FelipeBert.api.domain.validacoes;

import org.FelipeBert.api.domain.dto.in.CadastrarConsultaDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ValidadorHorarioAntecedencia implements ValidadorAgendamentoDeConsulta{

    public void validar(CadastrarConsultaDTO dados){
        if (dados.hora().isBefore(LocalDateTime.now().plusMinutes(30))) {
            throw new IllegalArgumentException("Consultas devem ser agendadas com no mínimo 30 minutos de antecedência.");
        }
    }

}
