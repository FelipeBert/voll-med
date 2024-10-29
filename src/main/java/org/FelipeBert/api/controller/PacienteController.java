package org.FelipeBert.api.controller;

import jakarta.validation.Valid;
import org.FelipeBert.api.domain.dto.in.AtualizarPacienteDTO;
import org.FelipeBert.api.domain.dto.in.CadastrarPacienteDTO;
import org.FelipeBert.api.domain.dto.out.DadosListagemPacienteDTO;
import org.FelipeBert.api.domain.service.PacienteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity cadastrarPaciente(@RequestBody @Valid CadastrarPacienteDTO dadosPaciente, UriComponentsBuilder uriBuilder){
        var paciente = service.cadastrarPaciente(dadosPaciente);

        var uri = uriBuilder.path("/pacientes/{id}").buildAndExpand(paciente.getId()).toUri();

        return ResponseEntity.created(uri).body(new DadosListagemPacienteDTO(paciente));
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemPacienteDTO>> listarPacientes(@PageableDefault(size = 10, sort = {"nome"}) Pageable pageable){
        var page = service.listarPacientes(pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping
    public ResponseEntity atualizarPaciente(@RequestBody @Valid AtualizarPacienteDTO dadosPaciente){
        var paciente = service.atualizarPaciente(dadosPaciente);

        return ResponseEntity.ok(new DadosListagemPacienteDTO(paciente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity excluirPaciente(@PathVariable Long id){
        service.excluirPaciente(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity detalharPaciente(@PathVariable Long id){
        var paciente = service.detalharPaciente(id);

        return ResponseEntity.ok(new DadosListagemPacienteDTO(paciente));
    }
}
