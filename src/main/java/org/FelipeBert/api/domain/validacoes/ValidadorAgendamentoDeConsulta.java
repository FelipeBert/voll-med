package org.FelipeBert.api.domain.validacoes;

import org.FelipeBert.api.domain.dto.in.CadastrarConsultaDTO;

public interface ValidadorAgendamentoDeConsulta {

    void validar(CadastrarConsultaDTO dados);
}
