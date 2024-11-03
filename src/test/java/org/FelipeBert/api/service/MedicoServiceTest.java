package org.FelipeBert.api.service;

import jakarta.persistence.EntityNotFoundException;
import org.FelipeBert.api.domain.dto.in.AtualizarMedicoDTO;
import org.FelipeBert.api.domain.dto.in.CadastroMedicoDTO;
import org.FelipeBert.api.domain.dto.in.EnderecoDTO;
import org.FelipeBert.api.domain.dto.out.DadosListagemMedicoDTO;
import org.FelipeBert.api.domain.model.Especialidade;
import org.FelipeBert.api.domain.model.Medico;
import org.FelipeBert.api.domain.service.MedicoService;
import org.FelipeBert.api.infra.repository.MedicoRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MedicoServiceTest {

    private MedicoRepository repository;
    private MedicoService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(MedicoRepository.class);
        service = new MedicoService(repository);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar Médico quando as informações fornecidas forem válidas")
    void cadastrarMedicoCenario1(){
        CadastroMedicoDTO dados = new CadastroMedicoDTO(
                "Dr. John Doe",
                "john.doe@example.com",
                "123456",
                "1234567890",
                Especialidade.CARDIOLOGIA,
                dadosEndereco(),
                LocalDateTime.now()
        );

        Medico medico = service.cadastrarMedico(dados);

        Mockito.verify(repository).save(Mockito.any(Medico.class));
        assertNotNull(medico);
        assertEquals("Dr. John Doe", medico.getNome());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve lançar uma exceção ao passar parâmetros nulos")
    void cadastrarMedicoCenario2() {
        CadastroMedicoDTO dados = new CadastroMedicoDTO(
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertThrows(NullPointerException.class, () -> {
            service.cadastrarMedico(dados);
        });
    }

    @Test
    @WithMockUser
    @DisplayName("Deve falhar ao cadastrar médico com dados obrigatórios ausentes")
    void cadastrarMedicoCenario3() {
        CadastroMedicoDTO dados = new CadastroMedicoDTO(
                "",
                "invalid-email",
                "123",
                "",
                null,
                null,
                null
        );

        assertThrows(NullPointerException.class, () -> {
            service.cadastrarMedico(dados);
        });

        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Medico.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar uma lista de Médicos conforme número da página e tamanho")
    void listarMedicosCenario1() {
        Pageable pageable = PageRequest.of(0, 10);

        var dados = new CadastroMedicoDTO("Dr. John Doe", "john.doe@example.com", "12345", "1234567",
                Especialidade.CARDIOLOGIA, dadosEndereco(), LocalDateTime.now());

        var dados2 = new CadastroMedicoDTO("Dr. Jane Smith", "jane.smith@example.com", "67890", "123456",
                Especialidade.DERMATOLOGIA, dadosEndereco(), LocalDateTime.now());

        List<Medico> medicos = List.of(
                new Medico(dados),
                new Medico(dados2)
        );
        Page<Medico> medicoPage = new PageImpl<>(medicos, pageable, medicos.size());

        Mockito.when(repository.findAllByAtivoTrue(pageable)).thenReturn(medicoPage);

        Page<DadosListagemMedicoDTO> result = service.listarMedicos(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("Dr. John Doe", result.getContent().get(0).nome());
        assertEquals("Dr. Jane Smith", result.getContent().get(1).nome());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar página vazia quando não houver médicos ativos")
    void listarMedicosCenario2() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Medico> emptyPage = Page.empty(pageable);

        Mockito.when(repository.findAllByAtivoTrue(pageable)).thenReturn(emptyPage);

        Page<DadosListagemMedicoDTO> result = service.listarMedicos(pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve atualizar o Médico quando as informações forem válidas")
    void atualizarMedicoCenario1() {
        Long medicoId = 1L;
        AtualizarMedicoDTO atualizarMedicoDTO = new AtualizarMedicoDTO(medicoId, "Updated Name", "123456789", null);
        Medico existingMedico = new Medico();
        existingMedico.setId(medicoId);
        existingMedico.setNome("Old Name");

        Mockito.when(repository.findById(medicoId)).thenReturn(Optional.of(existingMedico));

        Medico updatedMedico = service.atualizarMedico(atualizarMedicoDTO);

        assertEquals("Updated Name", updatedMedico.getNome());
        Mockito.verify(repository).save(existingMedico);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve lançar exceção ao tentar atualizar um médico inexistente")
    void atualizarMedicoCenario2() {
        Long medicoId = 1L;
        AtualizarMedicoDTO atualizarMedicoDTO = new AtualizarMedicoDTO(medicoId, "Updated Name", "123456789", null);

        Mockito.when(repository.findById(medicoId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            service.atualizarMedico(atualizarMedicoDTO);
        });
    }

    @Test
    @WithMockUser
    @DisplayName("Deve excluir o médico quando as informações forem válidas")
    void excluirMedicoCenario1() {
        Medico medico = new Medico();
        medico.setId(1L);
        medico.setAtivo(true);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(medico));

        service.excluirMedico(1L);

        assertFalse(medico.isAtivo());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve gerar uma exceção quando parâmetros forem inválidos")
    void excluirMedicoCenario2() {
        Mockito.when(repository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            service.excluirMedico(2L);
        });
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