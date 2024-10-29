package org.FelipeBert.api.domain.dto.in;

import jakarta.validation.constraints.NotNull;

public record AtualizarPacienteDTO(
        @NotNull
        Long id,
        String nome,
        String telefone,
        EnderecoDTO endereco) {
}
