package org.FelipeBert.api.dto;

import jakarta.validation.constraints.NotNull;

public record AtualizarMedicoDTO(
        @NotNull
        Long id,
        String nome,
        String telefone,
        EnderecoDTO endereco) {
}
