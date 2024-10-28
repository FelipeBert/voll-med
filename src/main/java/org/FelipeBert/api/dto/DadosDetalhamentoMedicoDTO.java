package org.FelipeBert.api.dto;

import org.FelipeBert.api.model.Endereco;
import org.FelipeBert.api.model.Especialidade;
import org.FelipeBert.api.model.Medico;

public record DadosDetalhamentoMedicoDTO(
        Long id,
        String nome,
        String email,
        String crm,
        Especialidade especialidade,
        Endereco endereco) {

    public DadosDetalhamentoMedicoDTO(Medico medico){
        this(medico.getId(), medico.getNome(), medico.getEmail(), medico.getCrm(), medico.getEspecialidade(), medico.getEndereco());
    }
}
