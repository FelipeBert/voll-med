package org.FelipeBert.api.domain.validacoes;

import org.FelipeBert.api.domain.dto.in.CadastrarConsultaDTO;
import org.FelipeBert.api.domain.model.Especialidade;
import org.FelipeBert.api.infra.repository.ConsultaRepository;
import org.FelipeBert.api.infra.repository.MedicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class ValidaMedicoTest {

    private MedicoRepository medicoRepository;
    private ConsultaRepository consultaRepository;

    @BeforeEach
    void setUp(){
        medicoRepository = Mockito.mock(MedicoRepository.class);
        consultaRepository = Mockito.mock(ConsultaRepository.class);
    }

    @Test
    @DisplayName("Deve permitir agendamento quando médico está ativo")
    void medicoAtivoCenario1() {
        ValidarMedicoAtivo validarMedicoAtivo = new ValidarMedicoAtivo(medicoRepository);
        CadastrarConsultaDTO dados = new CadastrarConsultaDTO(1L, 2L, LocalDateTime.now(), Especialidade.CARDIOLOGIA);

        Mockito.when(medicoRepository.findAtivoById(2L)).thenReturn(true);

        assertDoesNotThrow(() -> validarMedicoAtivo.validar(dados));
    }

    @Test
    @DisplayName("Deve lançar exceção quando médico está inativo")
    void medicoAtivoCenario2() {
        ValidarMedicoAtivo validarMedicoAtivo = new ValidarMedicoAtivo(medicoRepository);
        CadastrarConsultaDTO dados = new CadastrarConsultaDTO(1L, 2L, LocalDateTime.now(), Especialidade.CARDIOLOGIA);

        Mockito.when(medicoRepository.findAtivoById(2L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            validarMedicoAtivo.validar(dados);
        });
    }

    @Test
    @DisplayName("Quando ID do médico é null, deve retornar sem validação")
    void medicoAtivoCenario3() {
        ValidarMedicoAtivo validarMedicoAtivo = new ValidarMedicoAtivo(medicoRepository);
        CadastrarConsultaDTO dados = new CadastrarConsultaDTO(1L, null, LocalDateTime.now(), Especialidade.CARDIOLOGIA);

        assertDoesNotThrow(() -> validarMedicoAtivo.validar(dados));
    }

    @Test
    @DisplayName("Deve permitir agendamento quando médico não possui outra consulta no mesmo horário")
    void medicoComOutraConsultaCenario1() {
        ValidadorMedicoComOutraConsulta validador = new ValidadorMedicoComOutraConsulta(consultaRepository);

        CadastrarConsultaDTO dados = new CadastrarConsultaDTO(1L, 2L, LocalDateTime.of(2023, 10,
                10, 10, 0), null);

        Mockito.when(consultaRepository.existsByMedicoIdAndDataAndHora(2L, LocalDate.of(2023, 10, 10), LocalTime.of(10, 0)))
                .thenReturn(false);

        assertDoesNotThrow(() -> validador.validar(dados));
    }

    @Test
    @DisplayName("Deve lançar exceção quando médico possui outra consulta no mesmo horário")
    void medicoComOutraConsultaCenario2() {
        ValidadorMedicoComOutraConsulta validador = new ValidadorMedicoComOutraConsulta(consultaRepository);

        CadastrarConsultaDTO dados = new CadastrarConsultaDTO(1L, 2L, LocalDateTime.of(2023, 10,
                10, 10, 0), null);

        Mockito.when(consultaRepository.existsByMedicoIdAndDataAndHora(2L, LocalDate.of(2023, 10, 10), LocalTime.of(10, 0)))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            validador.validar(dados);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção quando DTO de consulta é null")
    void medicoComOutraConsultaCenario3() {
        ValidadorMedicoComOutraConsulta validador = new ValidadorMedicoComOutraConsulta(consultaRepository);

        CadastrarConsultaDTO dados = new CadastrarConsultaDTO(null, null, null, null);

        assertThrows(NullPointerException.class, () -> validador.validar(dados));
    }
}
