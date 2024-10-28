package org.FelipeBert.api.service;

import org.FelipeBert.api.dto.AtualizarMedicoDTO;
import org.FelipeBert.api.dto.CadastroMedicoDTO;
import org.FelipeBert.api.dto.DadosListagemMedicoDTO;
import org.FelipeBert.api.model.Endereco;
import org.FelipeBert.api.model.Medico;
import org.FelipeBert.api.repository.MedicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MedicoService {

    private MedicoRepository repository;

    public MedicoService(MedicoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Medico cadastrarMedico(CadastroMedicoDTO dados){
        var medico = new Medico(dados);
        repository.save(medico);

        return medico;
    }

    public Page<DadosListagemMedicoDTO> listarMedicos(Pageable paginacao) {
        return repository.findAllByAtivoTrue(paginacao).map(DadosListagemMedicoDTO::new);
    }

    public Medico detalharMedico(Long id){
        return buscaMedico(id);
    }

    @Transactional
    public Medico atualizarMedico(AtualizarMedicoDTO dados) {
        Medico medico = buscaMedico(dados.id());
        validaDados(medico, dados);
        repository.save(medico);
        return medico;
    }

    @Transactional
    public void excluirMedico(Long id) {
        Medico medico = buscaMedico(id);
        medico.setAtivo(false);
    }

    private void validaDados(Medico medico, AtualizarMedicoDTO dados){
        if(dados.nome() != null && !dados.nome().isEmpty()){
            medico.setNome(dados.nome());
        }
        if(dados.telefone() != null && !dados.telefone().isEmpty()){
            medico.setTelefone(dados.telefone());
        }
        if(dados.endereco() != null){
            medico.setEndereco(new Endereco(dados.endereco()));
        }
    }

    private Medico buscaMedico(Long id){
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Medico não encontrado."));
    }
}
