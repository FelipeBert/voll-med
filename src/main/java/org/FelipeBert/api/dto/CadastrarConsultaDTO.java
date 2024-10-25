package org.FelipeBert.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CadastrarConsultaDTO(
        @NotNull
        Long idPaciente,
        @NotNull
        Long idMedico,
        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime hora) {
}
