package org.FelipeBert.api.controller;

import jakarta.validation.Valid;
import org.FelipeBert.api.dto.AtualizarMedicoDTO;
import org.FelipeBert.api.dto.CadastroMedicoDTO;
import org.FelipeBert.api.dto.DadosListagemMedicoDTO;
import org.FelipeBert.api.service.MedicoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    private MedicoService service;

    public MedicoController(MedicoService service) {
        this.service = service;
    }

    @PostMapping
    public void cadastrarMedico(@RequestBody @Valid CadastroMedicoDTO dados) {
        service.cadastrarMedico(dados);
    }

    @GetMapping
    public Page<DadosListagemMedicoDTO> listarMedicos(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao){
        return service.listarMedicos(paginacao);
    }

    @PutMapping
    public void atualizarMedico(@RequestBody @Valid AtualizarMedicoDTO dados){
        service.atualizarMedico(dados);
    }

    @DeleteMapping("/{id}")
    public void excluirMedico(@PathVariable Long id){
        service.excluirMedico(id);
    }
}
