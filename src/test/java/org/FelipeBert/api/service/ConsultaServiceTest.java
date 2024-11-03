package org.FelipeBert.api.service;

import jakarta.persistence.EntityNotFoundException;
import org.FelipeBert.api.domain.dto.in.CadastrarConsultaDTO;
import org.FelipeBert.api.domain.dto.in.CancelarConsultaDTO;
import org.FelipeBert.api.domain.model.Consulta;
import org.FelipeBert.api.domain.model.Medico;
import org.FelipeBert.api.domain.model.Paciente;
import org.FelipeBert.api.domain.service.ConsultaService;
import org.FelipeBert.api.domain.validacoes.ValidadorAgendamentoDeConsulta;
import org.FelipeBert.api.infra.repository.ConsultaRepository;
import org.FelipeBert.api.infra.repository.MedicoRepository;
import org.FelipeBert.api.infra.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class ConsultaServiceTest {

    private PacienteRepository pacienteRepository;
    private MedicoRepository medicoRepository;
    private ConsultaRepository consultaRepository;
    private ValidadorAgendamentoDeConsulta validador;
    private ConsultaService consultaService;
    private List<ValidadorAgendamentoDeConsulta> validadores;

    @BeforeEach
    void setUp(){
        pacienteRepository = mock(PacienteRepository.class);
        medicoRepository = mock(MedicoRepository.class);
        consultaRepository = mock(ConsultaRepository.class);
        validador = mock(ValidadorAgendamentoDeConsulta.class);
        validadores = List.of(validador);

        consultaService = new ConsultaService(pacienteRepository, medicoRepository, consultaRepository, validadores);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve criar uma consulta com sucesso quando os parâmetros forem válidos")
    void agendarConsultaCenario1() {
        CadastrarConsultaDTO dados = new CadastrarConsultaDTO(1L, 1L, LocalDateTime.now(), null);
        Paciente paciente = new Paciente();
        Medico medico = new Medico();

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(medicoRepository.existsById(1L)).thenReturn(true);
        when(medicoRepository.getReferenceById(1L)).thenReturn(medico);
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Consulta consulta = consultaService.agendarConsulta(dados);

        assertNotNull(consulta);
        assertEquals(paciente, consulta.getPaciente());
        assertEquals(medico, consulta.getMedico());
        assertTrue(consulta.isMarcada());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve lançar uma exceção quando o paciente não existir")
    void agendarConsultaCenario2() {
        CadastrarConsultaDTO dados = new CadastrarConsultaDTO(1L, null, LocalDateTime.now(), null);

        when(pacienteRepository.findById(1L)).thenReturn(Optional.empty());

        ConsultaService consultaService = new ConsultaService(pacienteRepository, medicoRepository, consultaRepository, validadores);

        assertThrows(EntityNotFoundException.class, () -> {
            consultaService.agendarConsulta(dados);
        });
    }

    @Test
    @WithMockUser
    @DisplayName("Deve lançar uma exceção quando o medico não existir")
    void agendarConsultaCenario3() {
        CadastrarConsultaDTO dados = new CadastrarConsultaDTO(1L, 999L, LocalDateTime.now(), null);

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(new Paciente()));
        when(medicoRepository.existsById(999L)).thenReturn(false);

        ConsultaService consultaService = new ConsultaService(pacienteRepository, medicoRepository, consultaRepository, validadores);

        assertThrows(EntityNotFoundException.class, () -> consultaService.agendarConsulta(dados));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve cancelar a consulta com sucesso quando os parâmetros forem válidos")
    void cancelarConsultaCenario1() {
        Consulta consulta = new Consulta();
        consulta.setData(LocalDate.now().plusDays(2));
        consulta.setHora(LocalTime.now());
        when(consultaRepository.getReferenceById(1L)).thenReturn(consulta);

        CancelarConsultaDTO cancelarConsultaDTO = new CancelarConsultaDTO(1L, "Pedido do paciente");

        Consulta result = consultaService.cancelarConsulta(cancelarConsultaDTO);

        assertNotNull(result);
        assertFalse(result.isMarcada());
        assertEquals("Pedido do paciente", result.getMotivoCancelamento());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar null quando a tentativa de cancelamento ocorrer com menos de 24 horas de antecedência")
    void cancelarConsultaCenario2() {
        Consulta consulta = new Consulta();
        consulta.setData(LocalDate.now().plusDays(1));
        consulta.setHora(LocalTime.now());
        when(consultaRepository.getReferenceById(1L)).thenReturn(consulta);

        CancelarConsultaDTO cancelarConsultaDTO = new CancelarConsultaDTO(1L, "Pedido do paciente");

        Consulta result = consultaService.cancelarConsulta(cancelarConsultaDTO);

        assertNull(result);
    }

}