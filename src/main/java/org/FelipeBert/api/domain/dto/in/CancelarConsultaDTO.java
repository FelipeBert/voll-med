package org.FelipeBert.api.domain.dto.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CancelarConsultaDTO(
        @NotNull
        Long id,
        @NotBlank
        String motivo) {
}
