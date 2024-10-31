package org.FelipeBert.api.domain.validacoes;

import org.FelipeBert.api.domain.dto.in.CadastrarConsultaDTO;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Component
public class ValidadorHorarioFuncionamentoClinica implements ValidadorAgendamentoDeConsulta{

    private static final LocalTime CLINICA_ABERTURA = LocalTime.of(7, 0);
    private static final LocalTime CLINICA_FECHAMENTO = LocalTime.of(19, 0);

    public void validar(CadastrarConsultaDTO dados){
        if (dados.hora().getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("A clínica não funciona aos domingos.");
        }

        LocalTime hora = dados.hora().toLocalTime();

        if (hora.isBefore(CLINICA_ABERTURA) || hora.isAfter(CLINICA_FECHAMENTO.minusHours(1))) {
            throw new IllegalArgumentException("A clínica funciona das 07:00 às 19:00.");
        }
    }
}
