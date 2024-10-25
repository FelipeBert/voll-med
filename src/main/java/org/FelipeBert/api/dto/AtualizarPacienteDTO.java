package org.FelipeBert.api.dto;

import jakarta.validation.constraints.NotNull;

public record AtualizarPacienteDTO(
        @NotNull
        Long id,
        String nome,
        String telefone,
        EnderecoDTO endereco) {
}
