package org.FelipeBert.api.controller;

import jakarta.validation.Valid;
import org.FelipeBert.api.dto.AtualizarMedicoDTO;
import org.FelipeBert.api.dto.CadastroMedicoDTO;
import org.FelipeBert.api.dto.DadosListagemMedicoDTO;
import org.FelipeBert.api.model.Medico;
import org.FelipeBert.api.repository.MedicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    private MedicoRepository repository;

    public MedicoController(MedicoRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @Transactional
    public void cadastrarMedico(@RequestBody @Valid CadastroMedicoDTO dados) {
        repository.save(new Medico(dados));
    }

    @GetMapping
    public Page<DadosListagemMedicoDTO> listarMedicos(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao){
        return repository.findAllByAtivoTrue(paginacao).map(DadosListagemMedicoDTO::new);
    }

    @PutMapping
    @Transactional
    public void atualizarMedico(@RequestBody @Valid AtualizarMedicoDTO dados){
        var medico = repository.getReferenceById(dados.id());
        medico.atualizarDados(dados);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void excluirMedico(@PathVariable Long id){
        var medico = repository.getReferenceById(id);
        medico.desativarUsuario();
    }
}
