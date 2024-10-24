package org.FelipeBert.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.FelipeBert.api.model.Especialidade;

public record CadastroMedicoDTO(
        @NotBlank
        String nome,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Pattern(regexp = "\\d{4,6}")
        String crm,

        @NotBlank
        String telefone,

        @NotNull
        Especialidade especialidade,

        @NotNull
        @Valid
        EnderecoDTO endereco) {
}