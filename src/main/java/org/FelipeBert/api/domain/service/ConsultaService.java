package org.FelipeBert.api.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.FelipeBert.api.domain.dto.in.CadastrarConsultaDTO;
import org.FelipeBert.api.domain.dto.in.CancelarConsultaDTO;
import org.FelipeBert.api.domain.model.Consulta;
import org.FelipeBert.api.domain.model.Medico;
import org.FelipeBert.api.domain.model.Paciente;
import org.FelipeBert.api.infra.repository.ConsultaRepository;
import org.FelipeBert.api.infra.repository.MedicoRepository;
import org.FelipeBert.api.infra.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

@Service
public class ConsultaService {

    private PacienteRepository pacienteRepository;

    private MedicoRepository medicoRepository;

    private ConsultaRepository consultaRepository;

    public ConsultaService(PacienteRepository pacienteRepository, MedicoRepository medicoRepository, ConsultaRepository consultaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.consultaRepository = consultaRepository;
    }

    private static final LocalTime CLINICA_ABERTURA = LocalTime.of(7, 0);
    private static final LocalTime CLINICA_FECHAMENTO = LocalTime.of(19, 0);

    public Consulta agendarConsulta(CadastrarConsultaDTO dto) {
        Paciente paciente = pacienteRepository.findById(dto.idPaciente())
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado."));

        if (!paciente.isAtivo()) {
            throw new IllegalArgumentException("Não é possível agendar consultas para pacientes inativos.");
        }

        LocalDateTime dataHoraConsulta = dto.hora();
        validarDataHoraConsulta(dataHoraConsulta);
        validarConsultaPacienteDia(paciente, dataHoraConsulta.toLocalDate().atStartOfDay());

        Medico medico;
        if (dto.idMedico() != null) {
            medico = medicoRepository.findById(dto.idMedico())
                    .orElseThrow(() -> new EntityNotFoundException("Médico não encontrado."));
            if (!medico.isAtivo()) {
                throw new IllegalArgumentException("Não é possível agendar consultas com médicos inativos.");
            }
        } else {
            medico = selecionarMedicoAleatorioDisponivel(dataHoraConsulta);
        }

        validarDisponibilidadeMedico(medico, dataHoraConsulta);

        Consulta consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        consulta.setData(dataHoraConsulta.toLocalDate());
        consulta.setHora(dataHoraConsulta.toLocalTime());
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

    private void validarDataHoraConsulta(LocalDateTime dataHora) {
        if (dataHora.isBefore(LocalDateTime.now().plusMinutes(30))) {
            throw new IllegalArgumentException("Consultas devem ser agendadas com no mínimo 30 minutos de antecedência.");
        }

        if (dataHora.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("A clínica não funciona aos domingos.");
        }

        LocalTime hora = dataHora.toLocalTime();
        if (hora.isBefore(CLINICA_ABERTURA) || hora.isAfter(CLINICA_FECHAMENTO.minusMinutes(1))) {
            throw new IllegalArgumentException("A clínica funciona das 07:00 às 19:00.");
        }
    }

    private void validarConsultaPacienteDia(Paciente paciente, LocalDateTime data) {
        boolean jaTemConsulta = consultaRepository.existsByPacienteAndData(paciente, data.toLocalDate());
        if (jaTemConsulta) {
            throw new IllegalArgumentException("O paciente já possui uma consulta agendada para este dia.");
        }
    }

    private Medico selecionarMedicoAleatorioDisponivel(LocalDateTime dataHora) {
        List<Medico> medicosDisponiveis = medicoRepository.findDisponiveisByDataHora(dataHora);
        if (medicosDisponiveis.isEmpty()) {
            throw new IllegalArgumentException("Não há médicos disponíveis para o horário selecionado.");
        }
        return medicosDisponiveis.get(new Random().nextInt(medicosDisponiveis.size()));
    }

    private void validarDisponibilidadeMedico(Medico medico, LocalDateTime dataHora) {
        boolean medicoOcupado = consultaRepository.existsByMedicoAndDataAndHora(medico, dataHora.toLocalDate(), dataHora.toLocalTime());
        if (medicoOcupado) {
            throw new IllegalArgumentException("O médico já possui outra consulta agendada para este horário.");
        }
    }

    public Consulta detalharConsulta(Long id) {
        return consultaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Consulta não Encontrada!"));
    }
}
