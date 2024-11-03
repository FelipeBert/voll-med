package org.FelipeBert.api.domain.validacoes;

import org.FelipeBert.api.domain.dto.in.CadastrarConsultaDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ValidaFuncionamentoTest {

    @Test
    @DisplayName("Deve permitir agendamento quando o horário está com mais de 30 minutos de antecedência")
    void validarHorarioAntecedenciaCenario1() {
        ValidadorHorarioAntecedencia validador = new ValidadorHorarioAntecedencia();
        LocalDateTime futureTime = LocalDateTime.now().plusMinutes(31);
        CadastrarConsultaDTO dados = new CadastrarConsultaDTO(1L, 1L, futureTime, null);

        assertDoesNotThrow(() -> validador.validar(dados));
    }

    @Test
    @DisplayName("Não deve permitir agendamento quando o horário está com menos de 30 minutos de antecedência")
    void validarHorarioAntecedenciaCenario2() {
        ValidadorHorarioAntecedencia validador = new ValidadorHorarioAntecedencia();
        LocalDateTime nearFutureTime = LocalDateTime.now().plusMinutes(29);
        CadastrarConsultaDTO dados = new CadastrarConsultaDTO(1L, 1L, nearFutureTime, null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> validador.validar(dados));
        assertEquals("Consultas devem ser agendadas com no mínimo 30 minutos de antecedência.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve permitir agendamento entre segunda a sabado, entre os horarios 7 e 19")
    void validarHorarioFuncionamentoCenario1() {
        ValidadorHorarioFuncionamentoClinica validador = new ValidadorHorarioFuncionamentoClinica();
        CadastrarConsultaDTO consulta = new CadastrarConsultaDTO(
                1L,
                1L,
                LocalDateTime.of(2023, 10, 4, 10, 0),
                null
        );
        assertDoesNotThrow(() -> validador.validar(consulta));
    }

    @Test
    @DisplayName("Não deve permitir agendamento aos Domingos")
    void validarHorarioFuncionamentoCenario2() {
        ValidadorHorarioFuncionamentoClinica validador = new ValidadorHorarioFuncionamentoClinica();
        CadastrarConsultaDTO consulta = new CadastrarConsultaDTO(
                1L,
                1L,
                LocalDateTime.of(2023, 10, 8, 10, 0),
                null
        );
        Exception exception = assertThrows(IllegalArgumentException.class, () -> validador.validar(consulta));
        assertEquals("A clínica não funciona aos domingos.", exception.getMessage());
    }
}