package org.FelipeBert.api.service;

import jakarta.persistence.EntityNotFoundException;
import org.FelipeBert.api.domain.dto.in.AtualizarPacienteDTO;
import org.FelipeBert.api.domain.dto.in.CadastrarPacienteDTO;
import org.FelipeBert.api.domain.dto.in.EnderecoDTO;
import org.FelipeBert.api.domain.dto.out.DadosListagemPacienteDTO;
import org.FelipeBert.api.domain.model.Paciente;
import org.FelipeBert.api.domain.service.PacienteService;
import org.FelipeBert.api.infra.repository.PacienteRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class PacienteServiceTest {

    private PacienteRepository repository;
    private PacienteService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(PacienteRepository.class);
        service = new PacienteService(repository);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve cadastrar um paciente com dados válidos")
    void cadastrarPacienteCenario1() {
        CadastrarPacienteDTO dadosPaciente = new CadastrarPacienteDTO(
                "John Doe", "john.doe@example.com", "123456789", "12345678901", dadosEndereco()
        );

        Paciente paciente = service.cadastrarPaciente(dadosPaciente);

        assertNotNull(paciente);
        assertEquals("John Doe", paciente.getNome());
        assertEquals("john.doe@example.com", paciente.getEmail());
        assertEquals("123456789", paciente.getTelefone());
        assertEquals("12345678901", paciente.getCpf());
        verify(repository).save(any(Paciente.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve lançar exceção ao cadastrar paciente com dados inválidos")
    void cadastrarPacienteCenario2() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            CadastrarPacienteDTO dadosPaciente = new CadastrarPacienteDTO(
                    "", "invalid-email", "", "", null
            );
            service.cadastrarPaciente(dadosPaciente);
        });

        String expectedMessage = "Validation failed";
        String actualMessage = exception.getMessage();

        assertFalse(actualMessage.contains(expectedMessage));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar uma lista de pacientes paginada corretamente")
    void listarPacientesCenario1() {
        Pageable pageable = PageRequest.of(0, 10);

        var dados = new CadastrarPacienteDTO("John Doe", "john@example.com", "123456789", "12345678", dadosEndereco());
        Paciente paciente = new Paciente(dados);

        List<Paciente> pacientes = List.of(paciente);
        Page<Paciente> page = new PageImpl<>(pacientes, pageable, pacientes.size());

        when(repository.findAllByAtivoTrue(pageable)).thenReturn(page);

        Page<DadosListagemPacienteDTO> result = service.listarPacientes(pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("John Doe", result.getContent().get(0).nome());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar uma página vazia quando não houver pacientes ativos")
    void listarPacientesCenario2() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Paciente> emptyPage = Page.empty(pageable);

        when(repository.findAllByAtivoTrue(pageable)).thenReturn(emptyPage);

        Page<DadosListagemPacienteDTO> result = service.listarPacientes(pageable);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve atualizar os dados de um paciente com parâmetros válidos")
    void atualizarPacienteCenario1() {
        AtualizarPacienteDTO dadosPaciente = new AtualizarPacienteDTO(1L, "Updated Name", "123456789", null);
        Paciente existingPaciente = new Paciente();
        existingPaciente.setId(1L);
        existingPaciente.setNome("Old Name");

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existingPaciente));

        Paciente updatedPaciente = service.atualizarPaciente(dadosPaciente);

        Assertions.assertEquals("Updated Name", updatedPaciente.getNome());
        Mockito.verify(repository).save(existingPaciente);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve lançar exceção ao tentar atualizar paciente que não existe")
    void atualizarPacienteCenario2() {
        AtualizarPacienteDTO dadosPaciente = new AtualizarPacienteDTO(999L, "Updated Name", "123456789", null);

        Mockito.when(repository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            service.atualizarPaciente(dadosPaciente);
        });
    }

    @Test
    @WithMockUser
    @DisplayName("Deve realizar a exclusão lógica de um paciente com ID válido")
    void excluirPacienteCenario1() {
        Long pacienteId = 1L;
        Paciente paciente = new Paciente();
        paciente.setId(pacienteId);
        paciente.setAtivo(true);

        when(repository.findById(pacienteId)).thenReturn(Optional.of(paciente));

        service.excluirPaciente(pacienteId);

        assertFalse(paciente.isAtivo());
        verify(repository).findById(pacienteId);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve lançar exceção ao tentar excluir um paciente que não existe")
    void excluirPacienteCenario2() {
        Long nonExistentId = 999L;

        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            service.excluirPaciente(nonExistentId);
        });

        verify(repository).findById(nonExistentId);
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