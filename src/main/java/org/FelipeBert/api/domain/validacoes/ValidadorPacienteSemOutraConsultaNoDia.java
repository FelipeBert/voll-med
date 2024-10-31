package org.FelipeBert.api.domain.validacoes;

import org.FelipeBert.api.domain.dto.in.CadastrarConsultaDTO;
import org.FelipeBert.api.infra.repository.ConsultaRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidadorPacienteSemOutraConsultaNoDia implements ValidadorAgendamentoDeConsulta{

    private ConsultaRepository consultaRepository;

    public ValidadorPacienteSemOutraConsultaNoDia(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    public void validar(CadastrarConsultaDTO dados){
        boolean jaTemConsulta = consultaRepository.existsByPacienteIdAndData(dados.idPaciente(), dados.hora().toLocalDate());
        if (jaTemConsulta) {
            throw new IllegalArgumentException("O paciente já possui uma consulta agendada para este dia.");
        }
    }
}
