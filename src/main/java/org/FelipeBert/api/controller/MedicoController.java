package org.FelipeBert.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.FelipeBert.api.domain.dto.in.AtualizarMedicoDTO;
import org.FelipeBert.api.domain.dto.in.CadastroMedicoDTO;
import org.FelipeBert.api.domain.dto.out.DadosDetalhamentoMedicoDTO;
import org.FelipeBert.api.domain.dto.out.DadosListagemMedicoDTO;
import org.FelipeBert.api.domain.service.MedicoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/medicos")
@SecurityRequirement(name = "bearer-key")
public class MedicoController {

    private MedicoService service;

    public MedicoController(MedicoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity cadastrarMedico(@RequestBody @Valid CadastroMedicoDTO dados, UriComponentsBuilder uriBuilder) {
        var medico = service.cadastrarMedico(dados);

        var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoMedicoDTO(medico));
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemMedicoDTO>> listarMedicos(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao){
        var page = service.listarMedicos(paginacao);

        return ResponseEntity.ok(page);
    }

    @PutMapping
    public ResponseEntity atualizarMedico(@RequestBody @Valid AtualizarMedicoDTO dados){
        var medico = service.atualizarMedico(dados);

        return ResponseEntity.ok(new DadosDetalhamentoMedicoDTO(medico));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity excluirMedico(@PathVariable Long id){
        service.excluirMedico(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity detalharMedico(@PathVariable Long id){
        var medico = service.detalharMedico(id);

        return ResponseEntity.ok(new DadosDetalhamentoMedicoDTO(medico));
    }
}
