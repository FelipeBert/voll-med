package org.FelipeBert.api.domain.dto.out;

import org.FelipeBert.api.domain.model.Especialidade;
import org.FelipeBert.api.domain.model.Medico;

public record DadosListagemMedicoDTO(Long id, String nome, String email, String crm, Especialidade especialidade) {
    public DadosListagemMedicoDTO(Medico medico){
        this(medico.getId(), medico.getNome(), medico.getEmail(), medico.getCrm(), medico.getEspecialidade());
    }
}
