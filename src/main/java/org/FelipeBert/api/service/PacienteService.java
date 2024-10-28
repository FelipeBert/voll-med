package org.FelipeBert.api.service;

import org.FelipeBert.api.dto.AtualizarPacienteDTO;
import org.FelipeBert.api.dto.CadastrarPacienteDTO;
import org.FelipeBert.api.dto.DadosListagemPacienteDTO;
import org.FelipeBert.api.model.Endereco;
import org.FelipeBert.api.model.Paciente;
import org.FelipeBert.api.repository.PacienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PacienteService {
    private PacienteRepository repository;

    public PacienteService(PacienteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Paciente cadastrarPaciente(CadastrarPacienteDTO dadosPaciente) {
        var paciente = new Paciente(dadosPaciente);
        repository.save(paciente);

        return paciente;
    }

    public Page<DadosListagemPacienteDTO> listarPacientes(Pageable pageable) {
        return repository.findAllByAtivoTrue(pageable).map(DadosListagemPacienteDTO::new);
    }

    @Transactional
    public Paciente atualizarPaciente(AtualizarPacienteDTO dadosPaciente) {
        Paciente paciente = buscarPaciente(dadosPaciente.id());

        validaDados(paciente, dadosPaciente);
        repository.save(paciente);

        return paciente;
    }

    @Transactional
    public void excluirPaciente(Long id){
        Paciente paciente = buscarPaciente(id);
        paciente.setAtivo(false);
    }

    private void validaDados(Paciente paciente, AtualizarPacienteDTO dadosPaciente){
        if(dadosPaciente.endereco() != null){
            paciente.setEndereco(new Endereco(dadosPaciente.endereco()));
        }
        if(dadosPaciente.nome() != null && !dadosPaciente.nome().isEmpty()){
            paciente.setNome(dadosPaciente.nome());
        }
        if(dadosPaciente.telefone() != null && !dadosPaciente.telefone().isEmpty()){
            paciente.setTelefone(dadosPaciente.telefone());
        }
    }

    public Paciente detalharPaciente(Long id) {
        return buscarPaciente(id);
    }

    private Paciente buscarPaciente(Long id){
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Paciente não Encontrado!"));
    }
}
