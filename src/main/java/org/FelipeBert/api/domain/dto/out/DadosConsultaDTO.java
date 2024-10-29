package org.FelipeBert.api.domain.dto.out;

import org.FelipeBert.api.domain.model.Consulta;
import org.FelipeBert.api.domain.model.Medico;
import org.FelipeBert.api.domain.model.Paciente;

import java.time.LocalDate;
import java.time.LocalTime;

public record DadosConsultaDTO(
        Long id,
        LocalDate data,
        LocalTime hora,
        boolean marcada,
        Medico medico,
        Paciente paciente) {
    public DadosConsultaDTO(Consulta consulta){
        this(consulta.getId(), consulta.getData(), consulta.getHora(), consulta.isMarcada(), consulta.getMedico(), consulta.getPaciente());
    }
}
