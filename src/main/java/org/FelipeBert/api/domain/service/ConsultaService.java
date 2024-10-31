package org.FelipeBert.api.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.FelipeBert.api.domain.dto.in.CadastrarConsultaDTO;
import org.FelipeBert.api.domain.dto.in.CancelarConsultaDTO;
import org.FelipeBert.api.domain.model.Consulta;
import org.FelipeBert.api.domain.model.Medico;
import org.FelipeBert.api.domain.model.Paciente;
import org.FelipeBert.api.domain.validacoes.ValidadorAgendamentoDeConsulta;
import org.FelipeBert.api.infra.repository.ConsultaRepository;
import org.FelipeBert.api.infra.repository.MedicoRepository;
import org.FelipeBert.api.infra.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultaService {

    private PacienteRepository pacienteRepository;

    private MedicoRepository medicoRepository;

    private ConsultaRepository consultaRepository;

    private List<ValidadorAgendamentoDeConsulta> validadores;

    public ConsultaService(PacienteRepository pacienteRepository, MedicoRepository medicoRepository,
                           ConsultaRepository consultaRepository, List<ValidadorAgendamentoDeConsulta> validadores) {
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.consultaRepository = consultaRepository;
        this.validadores = validadores;
    }

    @Transactional
    public Consulta agendarConsulta(CadastrarConsultaDTO dados) {
        Paciente paciente = pacienteRepository.findById(dados.idPaciente())
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado."));

        if(dados.idMedico() != null && !medicoRepository.existsById(dados.idMedico())){
            throw new EntityNotFoundException("Medico não encontrado!");
        }

        Medico medico = escolherMedico(dados);

        validadores.forEach(v -> v.validar(dados));

        Consulta consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        consulta.setData(dados.hora().toLocalDate());
        consulta.setHora(dados.hora().toLocalTime());
        consulta.setMarcada(true);

        return consultaRepository.save(consulta);
    }

    @Transactional
    public Consulta cancelarConsulta(CancelarConsultaDTO dadosCancelamento) {
        var consulta = consultaRepository.getReferenceById(dadosCancelamento.id());
        LocalDateTime dataHoraConsulta = LocalDateTime.of(consulta.getData(), consulta.getHora());

        if(LocalDateTime.now().isBefore(dataHoraConsulta.minusHours(24))){
            consulta.setMarcada(false);
            consulta.setMotivoCancelamento(dadosCancelamento.motivo());
            return consulta;
        }
        return null;
    }

    public Consulta detalharConsulta(Long id) {
        return consultaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Consulta não Encontrada!"));
    }

    private Medico escolherMedico(CadastrarConsultaDTO dados) {
        if (dados.idMedico() != null) {
            return medicoRepository.getReferenceById(dados.idMedico());
        }

        if (dados.especialidade() == null) {
            throw new IllegalArgumentException("Especialidade é obrigatória quando médico não for escolhido!");
        }

        return medicoRepository.escolherMedicoPorEspecialidadeEDataHora(dados.hora().toLocalDate(), dados.hora().toLocalTime(), dados.especialidade());
    }
}
