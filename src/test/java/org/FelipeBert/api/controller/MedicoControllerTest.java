package org.FelipeBert.api.controller;

import jakarta.persistence.EntityNotFoundException;
import org.FelipeBert.api.domain.dto.in.AtualizarMedicoDTO;
import org.FelipeBert.api.domain.dto.in.CadastroMedicoDTO;
import org.FelipeBert.api.domain.dto.in.EnderecoDTO;
import org.FelipeBert.api.domain.dto.out.DadosDetalhamentoMedicoDTO;
import org.FelipeBert.api.domain.dto.out.DadosListagemMedicoDTO;
import org.FelipeBert.api.domain.model.Especialidade;
import org.FelipeBert.api.domain.model.Medico;
import org.FelipeBert.api.domain.service.MedicoService;
import org.FelipeBert.api.infra.repository.MedicoRepository;
import org.junit.jupiter.api.Assertions;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
public class MedicoControllerTest {

    private MedicoService medicoService;
    private MedicoController medicoController;
    private MedicoRepository medicoRepository;

    @BeforeEach
    void setUp(){
        medicoService = Mockito.mock(MedicoService.class);
        medicoRepository = Mockito.mock(MedicoRepository.class);
        medicoController = new MedicoController(medicoService);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar HTTP 201 quando parâmetros forem válidos")
    void cadastrarMedicoCenario1(){
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
        CadastroMedicoDTO dados = new CadastroMedicoDTO("Dr. John Doe", "john.doe@example.com", "123456", "1234567890",
                Especialidade.CARDIOLOGIA, dadosEndereco(), LocalDateTime.now());

        Medico medico = new Medico(dados);
        when(medicoService.cadastrarMedico(dados)).thenReturn(medico);

        ResponseEntity response = medicoController.cadastrarMedico(dados, uriBuilder);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(DadosDetalhamentoMedicoDTO.class, response.getBody());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve lançar exceção ao tentar criar medico quando parâmetros forem inválidos")
    void cadastrarMedicoCenario2(){
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
        CadastroMedicoDTO invalidDados = new CadastroMedicoDTO("", "invalid-email", "123", "",
                null, null, null);

        assertThrows(NullPointerException.class, () -> {
            medicoController.cadastrarMedico(invalidDados, uriBuilder);
        });
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar lista de médicos ativos corretamente")
    void listarMedicosCenario1(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));

        CadastroMedicoDTO dados = new CadastroMedicoDTO("Dr. John Doe", "john.doe@example.com", "123456", "1234567890",
                Especialidade.CARDIOLOGIA, dadosEndereco(), LocalDateTime.now());

        Page<Medico> medicoPage = new PageImpl<>(List.of(new Medico(dados)));

        Page<DadosListagemMedicoDTO> medicos = medicoPage.map(DadosListagemMedicoDTO::new);

        Mockito.when(medicoService.listarMedicos(pageable)).thenReturn(medicos);

        Page<DadosListagemMedicoDTO> result = medicoService.listarMedicos(pageable);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("Dr. John Doe", result.getContent().getFirst().nome());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar lista vazia quando não houver mais médicos ativos")
    void listarMedicosCenario2(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
        Page<DadosListagemMedicoDTO> emptyPage = new PageImpl<>(Collections.emptyList());
        Mockito.when(medicoService.listarMedicos(pageable)).thenReturn(emptyPage);

        Page<DadosListagemMedicoDTO> result = medicoService.listarMedicos(pageable);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve atualizar os dados do médico com sucesso")
    void atualizarMedicoCenario1(){
        AtualizarMedicoDTO dados = new AtualizarMedicoDTO(1L, "Dr. John Doe", "123456789", null);

        var dadosMedico = new CadastroMedicoDTO("Dr. John Doe", "john.doe@example.com", "12345", "1234567",
                Especialidade.CARDIOLOGIA, dadosEndereco(), LocalDateTime.now());

        Medico medico = new Medico(dadosMedico);

        when(medicoService.atualizarMedico(dados)).thenReturn(medico);

        ResponseEntity response = medicoController.atualizarMedico(dados);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        DadosDetalhamentoMedicoDTO responseBody = (DadosDetalhamentoMedicoDTO) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Dr. John Doe", responseBody.nome());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve lançar exceção ao tentar atualizar um médico inexistente")
    void atualizarMedicoCenario2(){
        AtualizarMedicoDTO dados = new AtualizarMedicoDTO(999L, "Dr. Jane Doe", "987654321", null);

        when(medicoService.atualizarMedico(dados)).thenThrow(new EntityNotFoundException("Medico nￃﾣo encontrado."));

        MedicoController controller = new MedicoController(medicoService);

        assertThrows(EntityNotFoundException.class, () -> {
            controller.atualizarMedico(dados);
        });
    }

    @Test
    @WithMockUser
    @DisplayName("Deve deletar o médico com sucesso")
    void deletarMedicoCenario1(){
        Long validId = 1L;
        Medico medico = new Medico();
        medico.setId(validId);
        medico.setAtivo(false);

        Mockito.when(medicoRepository.findById(validId)).thenReturn(Optional.of(medico));

        ResponseEntity response = medicoController.excluirMedico(validId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(medico.isAtivo());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve lançar exceção ao tentar deletar um médico inexistente")
    void deletarMedicoCenario2() {
        Long nonExistentId = 999L;

        doThrow(new EntityNotFoundException("Medico nￃﾣo Encontrado!")).when(medicoService).excluirMedico(nonExistentId);

        assertThrows(EntityNotFoundException.class, () -> {
            medicoController.excluirMedico(nonExistentId);
        });

        verify(medicoService, times(1)).excluirMedico(nonExistentId);
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