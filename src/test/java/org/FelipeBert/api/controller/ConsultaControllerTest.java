package org.FelipeBert.api.controller;

import jakarta.persistence.EntityNotFoundException;
import org.FelipeBert.api.domain.dto.in.CadastrarConsultaDTO;
import org.FelipeBert.api.domain.dto.in.CancelarConsultaDTO;
import org.FelipeBert.api.domain.model.Consulta;
import org.FelipeBert.api.domain.model.Medico;
import org.FelipeBert.api.domain.model.Paciente;
import org.FelipeBert.api.domain.service.ConsultaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class ConsultaControllerTest {

    private ConsultaService consultaService;
    private ConsultaController consultaController;

    @BeforeEach
    void setUp(){
        consultaService = mock(ConsultaService.class);
        consultaController = new ConsultaController(consultaService);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar HTTP 201 quando a consulta for agendada com sucesso")
    void agendarConsultaCenario1() {
        CadastrarConsultaDTO dadosConsulta = new CadastrarConsultaDTO(1L, 1L, LocalDateTime.now().plusDays(1), null);
        Consulta consulta = new Consulta(1L, LocalDate.now().plusDays(1), LocalTime.now(),
                true, new Medico(), new Paciente(), null);

        when(consultaService.agendarConsulta(dadosConsulta)).thenReturn(consulta);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();

        ResponseEntity response = new ConsultaController(consultaService).agendarConsulta(dadosConsulta, uriBuilder);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve lançar exceção quando os dados da consulta forem nulos")
    void agendarConsultaCenario2() {
        CadastrarConsultaDTO dadosConsulta = new CadastrarConsultaDTO(null, null, null, null);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();

        assertThrows(NullPointerException.class, () -> {
            consultaController.agendarConsulta(dadosConsulta, uriBuilder);
        });
    }

    @Test
    @WithMockUser
    @DisplayName("Deve cancelar a consulta com sucesso")
    void cancelarConsultaCenario1() {
        CancelarConsultaDTO dadosCancelamento = new CancelarConsultaDTO(1L, "Patient request");
        Consulta consulta = new Consulta();
        consulta.setId(1L);
        consulta.setMarcada(false);
        consulta.setPaciente(new Paciente());
        consulta.setMedico(new Medico());

        when(consultaService.cancelarConsulta(dadosCancelamento)).thenReturn(consulta);

        ResponseEntity response = consultaController.cancelarConsulta(dadosCancelamento);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve lançar exceção ao tentar cancelar uma consulta inexistente")
    void cancelarConsultaCenario2() {
        CancelarConsultaDTO dadosCancelamento = new CancelarConsultaDTO(999L, "Non-existent ID");
        when(consultaService.cancelarConsulta(dadosCancelamento)).thenThrow(new EntityNotFoundException("Consulta nￃﾣo Encontrada!"));

        assertThrows(EntityNotFoundException.class, () -> {
            consultaController.cancelarConsulta(dadosCancelamento);
        });
    }
}
