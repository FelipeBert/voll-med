package org.FelipeBert.api.domain.dto.in;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import org.FelipeBert.api.domain.model.Especialidade;

import java.time.LocalDateTime;

public record CadastrarConsultaDTO(
        @NotNull
        Long idPaciente,
        Long idMedico,
        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime hora,

        Especialidade especialidade) {
}
