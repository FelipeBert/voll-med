package org.FelipeBert.api.dto;

import org.FelipeBert.api.model.Especialidade;
import org.FelipeBert.api.model.Medico;

public record DadosListagemMedicoDTO(Long id, String nome, String email, String crm, Especialidade especialidade) {
    public DadosListagemMedicoDTO(Medico medico){
        this(medico.getId(), medico.getNome(), medico.getEmail(), medico.getCrm(), medico.getEspecialidade());
    }
}
