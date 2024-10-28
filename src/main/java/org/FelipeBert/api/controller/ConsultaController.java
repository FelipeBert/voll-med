package org.FelipeBert.api.controller;

import jakarta.validation.Valid;
import org.FelipeBert.api.dto.CadastrarConsultaDTO;
import org.FelipeBert.api.dto.CancelarConsultaDTO;
import org.FelipeBert.api.dto.DadosConsultaDTO;
import org.FelipeBert.api.service.ConsultaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private ConsultaService service;

    public ConsultaController(ConsultaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity agendarConsulta(@RequestBody @Valid CadastrarConsultaDTO dadosConsulta, UriComponentsBuilder uriBuilder){
       var consulta = service.agendarConsulta(dadosConsulta);

       var uri = uriBuilder.path("/consultas/{id}").buildAndExpand(consulta.getId()).toUri();

       return ResponseEntity.created(uri).body(new DadosConsultaDTO(consulta));
    }

    @PutMapping
    public ResponseEntity cancelarConsulta(@RequestBody  @Valid CancelarConsultaDTO dadosCancelamento){
        var consulta =  service.cancelarConsulta(dadosCancelamento);
        
        return ResponseEntity.ok(new DadosConsultaDTO(consulta));
    }

    @GetMapping("/{id}")
    public ResponseEntity detalharConsulta(@PathVariable Long id){
        var consulta = service.detalharConsulta(id);

        return ResponseEntity.ok(new DadosConsultaDTO(consulta));
    }
}
