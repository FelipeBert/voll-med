package org.FelipeBert.api.domain.dto.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.FelipeBert.api.domain.model.Endereco;

public record EnderecoDTO(
        @NotBlank
        String logradouro,
        @NotBlank
        String bairro,
        @NotBlank
        @Pattern(regexp = "\\d{8}")
        String cep,
        @NotBlank
        String cidade,
        @NotBlank
        String uf,
        String numero,
        String complemento) {

        public EnderecoDTO(Endereco endereco){
                this(endereco.getLogradouro(), endereco.getBairro(), endereco.getCep(), endereco.getCidade(), endereco.getUf(),
                        endereco.getNumero(), endereco.getComplemento());
        }
}