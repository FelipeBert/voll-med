package org.FelipeBert.api.domain.dto.in;

import jakarta.validation.constraints.NotBlank;

public record DadosAutenticacaoDTO(
        @NotBlank
        String login,
        @NotBlank
        String senha) {
}
