package org.FelipeBert.api.controller;

import jakarta.validation.Valid;
import org.FelipeBert.api.dto.AtualizarPacienteDTO;
import org.FelipeBert.api.dto.CadastrarPacienteDTO;
import org.FelipeBert.api.dto.DadosListagemPacienteDTO;
import org.FelipeBert.api.service.PacienteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    @PostMapping
    public void cadastrarPaciente(@RequestBody @Valid CadastrarPacienteDTO dadosPaciente){
        service.cadastrarPaciente(dadosPaciente);
    }

    @GetMapping
    public Page<DadosListagemPacienteDTO> listarPacientes(@PageableDefault(size = 10, sort = {"nome"}) Pageable pageable){
        return service.listarPacientes(pageable);
    }

    @PutMapping
    public void atualizarPaciente(@RequestBody @Valid AtualizarPacienteDTO dadosPaciente){
        service.atualizarPaciente(dadosPaciente);
    }

    @DeleteMapping("/{id}")
    public void excluirPaciente(@PathVariable Long id){
        service.excluirPaciente(id);
    }
}
