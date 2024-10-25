package org.FelipeBert.api.controller;

import jakarta.validation.Valid;
import org.FelipeBert.api.dto.CadastrarConsultaDTO;
import org.FelipeBert.api.dto.CancelarConsultaDTO;
import org.FelipeBert.api.service.ConsultaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private ConsultaService service;

    public ConsultaController(ConsultaService service) {
        this.service = service;
    }

    @PostMapping
    public void agendarConsulta(@RequestBody @Valid CadastrarConsultaDTO dadosConsulta){
       service.agendarConsulta(dadosConsulta);
    }

    @PutMapping
    public String cancelarConsulta(@RequestBody  @Valid CancelarConsultaDTO dadosCancelamento){
        return service.cancelarConsulta(dadosCancelamento);
    }
}
