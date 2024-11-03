package org.FelipeBert.api.domain.validacoes;

import org.FelipeBert.api.domain.dto.in.CadastrarConsultaDTO;
import org.FelipeBert.api.infra.repository.ConsultaRepository;
import org.FelipeBert.api.infra.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class ValidaPacienteTest {

    private PacienteRepository pacienteRepository;
    private ConsultaRepository consultaRepository;

    @BeforeEach
    void setUp(){
        pacienteRepository = Mockito.mock(PacienteRepository.class);
        consultaRepository = Mockito.mock(ConsultaRepository.class);
    }

    @Test
    @DisplayName("Deve permitir agendamento quando paciente está ativo")
    void pacienteAtivoCenario1() {
        ValidadorPacienteAtivo validador = new ValidadorPacienteAtivo(pacienteRepository);
        CadastrarConsultaDTO dados = new CadastrarConsultaDTO(1L, null, LocalDateTime.now(), null);

        Mockito.when(pacienteRepository.findAtivoById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> validador.validar(dados));
    }

    @Test
    @DisplayName("Deve lançar exceção quando paciente está inativo")
    void pacienteAtivoCenario2() {
        ValidadorPacienteAtivo validador = new ValidadorPacienteAtivo(pacienteRepository);
        CadastrarConsultaDTO dados = new CadastrarConsultaDTO(1L, null, LocalDateTime.now(), null);

        Mockito.when(pacienteRepository.findAtivoById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            validador.validar(dados);
        });
    }

    @Test
    @DisplayName("Deve permitir agendamento quando paciente não possui outra consulta no dia")
    void pacienteSemOutraConsultaNoDiaCenario1() {
        ValidadorPacienteSemOutraConsultaNoDia validador = new ValidadorPacienteSemOutraConsultaNoDia(consultaRepository);

        CadastrarConsultaDTO dados = new CadastrarConsultaDTO(1L, 2L, LocalDateTime.of(2023, 10,
                10, 10, 0), null);

        Mockito.when(consultaRepository.existsByPacienteIdAndData(1L, LocalDate.of(2023, 10, 10))).thenReturn(false);

        assertDoesNotThrow(() -> validador.validar(dados));
    }

    @Test
    @DisplayName("Deve lançar exceção quando paciente já possui outra consulta no dia")
    void pacienteSemOutraConsultaNoDiaCenario2() {
        ValidadorPacienteSemOutraConsultaNoDia validador = new ValidadorPacienteSemOutraConsultaNoDia(consultaRepository);

        CadastrarConsultaDTO dados = new CadastrarConsultaDTO(1L, 2L, LocalDateTime.of(2023, 10,
                10, 10, 0), null);

        Mockito.when(consultaRepository.existsByPacienteIdAndData(1L, LocalDate.of(2023, 10, 10))).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            validador.validar(dados);
        });
    }
}