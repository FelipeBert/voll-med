package org.FelipeBert.api.domain.dto.in;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.FelipeBert.api.domain.model.Especialidade;

import java.time.LocalDateTime;

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
        EnderecoDTO endereco,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime hora) {
}