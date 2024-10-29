package org.FelipeBert.api.domain.dto.in;

import jakarta.validation.constraints.NotNull;

public record AtualizarMedicoDTO(
        @NotNull
        Long id,
        String nome,
        String telefone,
        EnderecoDTO endereco) {
}
