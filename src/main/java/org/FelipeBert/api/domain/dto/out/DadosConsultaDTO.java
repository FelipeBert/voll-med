package org.FelipeBert.api.domain.dto.out;

import org.FelipeBert.api.domain.model.Consulta;

import java.time.LocalDate;
import java.time.LocalTime;

public record DadosConsultaDTO(
        Long id,
        LocalDate data,
        LocalTime hora,
        boolean marcada,
        DadosListagemMedicoDTO medico,
        DadosListagemPacienteDTO paciente) {

    public DadosConsultaDTO(Consulta consulta){
        this(consulta.getId(), consulta.getData(), consulta.getHora(), consulta.isMarcada(),
                new DadosListagemMedicoDTO(consulta.getMedico()), new DadosListagemPacienteDTO(consulta.getPaciente()));
    }
}
