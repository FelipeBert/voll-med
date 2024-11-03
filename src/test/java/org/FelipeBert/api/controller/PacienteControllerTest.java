package org.FelipeBert.api.controller;

import jakarta.persistence.EntityNotFoundException;
import org.FelipeBert.api.domain.dto.in.AtualizarPacienteDTO;
import org.FelipeBert.api.domain.dto.in.CadastrarPacienteDTO;
import org.FelipeBert.api.domain.dto.in.EnderecoDTO;
import org.FelipeBert.api.domain.dto.out.DadosListagemPacienteDTO;
import org.FelipeBert.api.domain.model.Paciente;
import org.FelipeBert.api.domain.service.PacienteService;
import org.FelipeBert.api.infra.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PacienteControllerTest {

    private PacienteRepository pacienteRepository;
    private PacienteService pacienteService;
    private PacienteController pacienteController;

    @BeforeEach
    void setUp(){
        pacienteRepository = mock(PacienteRepository.class);
        pacienteService = mock(PacienteService.class);
        pacienteController = new PacienteController(pacienteService);
    }

    @Test
    @WithMockUser
    @DisplayName("Cadastrar paciente com dados válidos")
    void cadastrarPacienteCenario1() {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();

        CadastrarPacienteDTO dadosPaciente = new CadastrarPacienteDTO(
                "John Doe", "john.doe@example.com", "1234567890", "123.456.789-00", dadosEndereco());

        Paciente paciente = new Paciente(dadosPaciente);
        when(pacienteService.cadastrarPaciente(dadosPaciente)).thenReturn(paciente);

        ResponseEntity response = pacienteController.cadastrarPaciente(dadosPaciente, uriBuilder);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof DadosListagemPacienteDTO);
    }

    @Test
    @WithMockUser
    @DisplayName("Cadastrar paciente com dados inválidos deve lançar NullPointerException")
    void cadastrarPacienteCenario2() {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
        CadastrarPacienteDTO dadosPaciente = new CadastrarPacienteDTO(
                "", "invalid-email", "", "", null
        );

        assertThrows(NullPointerException.class, () -> {
            pacienteController.cadastrarPaciente(dadosPaciente, uriBuilder);
        });
    }

    @Test
    @WithMockUser
    @DisplayName("Retornar lista de pacientes paginada e ordenada pelo nome")
    void listarPacientesCenario1() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
        Page<DadosListagemPacienteDTO> expectedPage = new PageImpl<>(List.of(
                new DadosListagemPacienteDTO(1L, "Alice", "alice@example.com", "12345678901"),
                new DadosListagemPacienteDTO(2L, "Bob", "bob@example.com", "23456789012")
        ));
        when(pacienteService.listarPacientes(pageable)).thenReturn(expectedPage);

        ResponseEntity<Page<DadosListagemPacienteDTO>> response = pacienteController.listarPacientes(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPage, response.getBody());
    }

    @Test
    @WithMockUser
    @DisplayName("Lidar graciosamente com lista de pacientes vazia")
    void listarPacientesCenario2() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
        Page<DadosListagemPacienteDTO> emptyPage = new PageImpl<>(Collections.emptyList());
        when(pacienteService.listarPacientes(pageable)).thenReturn(emptyPage);

        ResponseEntity<Page<DadosListagemPacienteDTO>> response = pacienteController.listarPacientes(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    @WithMockUser
    @DisplayName("Atualizar paciente com dados válidos")
    void atualizarPacienteCenario1() {
        AtualizarPacienteDTO dadosPaciente = new AtualizarPacienteDTO(1L, "John Doe", "123456789", null);

        var dados = new CadastrarPacienteDTO("Old Name", "teste@gmail.com", "123456", "12345678", dadosEndereco());

        Paciente paciente = new Paciente(dados);
        paciente.setId(1L);
        paciente.setNome("John Doe");

        Mockito.when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        Mockito.when(pacienteService.atualizarPaciente(Mockito.any(AtualizarPacienteDTO.class))).thenReturn(paciente);

        ResponseEntity response = pacienteController.atualizarPaciente(dadosPaciente);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        DadosListagemPacienteDTO responseBody = (DadosListagemPacienteDTO) response.getBody();
        assertNotNull(responseBody);
        assertEquals("John Doe", responseBody.nome());
    }

    @Test
    @WithMockUser
    @DisplayName("Atualizar paciente não encontrado deve lançar EntityNotFoundException")
    void atualizarPacienteCenario2() {
        AtualizarPacienteDTO dadosPaciente = new AtualizarPacienteDTO(999L, "John Doe", "123456789", null);

        Mockito.when(pacienteService.atualizarPaciente(Mockito.any(AtualizarPacienteDTO.class)))
                .thenThrow(new EntityNotFoundException("Paciente não encontrado."));

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            pacienteController.atualizarPaciente(dadosPaciente);
        });

        assertEquals("Paciente não encontrado.", exception.getMessage());
    }

    @Test
    @WithMockUser
    @DisplayName("Deletar paciente existente")
    void deletarPacienteCenario1() {
        Long validId = 1L;

        doNothing().when(pacienteService).excluirPaciente(validId);

        ResponseEntity response = new PacienteController(pacienteService).excluirPaciente(validId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(pacienteService, times(1)).excluirPaciente(validId);
    }

    @Test
    @WithMockUser
    @DisplayName("Deletar paciente não encontrado deve lançar EntityNotFoundException")
    void deletarPacienteCenario2() {
        Long nonExistentId = 999L;

        doThrow(new EntityNotFoundException("Paciente nￃﾣo Encontrado!")).when(pacienteService).excluirPaciente(nonExistentId);

        assertThrows(EntityNotFoundException.class, () -> {
            pacienteController.excluirPaciente(nonExistentId);
        });

        verify(pacienteService, times(1)).excluirPaciente(nonExistentId);
    }


    private EnderecoDTO dadosEndereco(){
        return new EnderecoDTO(
                "rua 1",
                "bairro",
                "12345677",
                "Brasilia",
                "DF",
                null,
                null
        );
    }
}
