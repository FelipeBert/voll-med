package org.FelipeBert.api.infra.repository;

import org.FelipeBert.api.domain.dto.in.CadastrarPacienteDTO;
import org.FelipeBert.api.domain.dto.in.CadastroMedicoDTO;
import org.FelipeBert.api.domain.dto.in.EnderecoDTO;
import org.FelipeBert.api.domain.model.Consulta;
import org.FelipeBert.api.domain.model.Especialidade;
import org.FelipeBert.api.domain.model.Medico;
import org.FelipeBert.api.domain.model.Paciente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class MedicoRepositoryTest {

    @Autowired
    private MedicoRepository repository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("Deveria devolver Null quando o unico medico cadastrado não esta disponivel na Data!")
    void escolherMedicoPorEspecialidadeEDataHoraCenario1() {

        var proximaSegundaAsDez = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atTime(10, 0);

        var medico = cadastrarMedico("tanto faz", "tanto.faz@gmail.com", "123456", Especialidade.CARDIOLOGIA);
        var paciente = cadastrarPaciente("tanto faz", "tanto.faz@gmail.com", "0000000");
        cadastrarConsulta(medico, paciente, proximaSegundaAsDez);

        var medicoLivre = repository.escolherMedicoPorEspecialidadeEDataHora(proximaSegundaAsDez.toLocalDate(),
                proximaSegundaAsDez.toLocalTime(), Especialidade.CARDIOLOGIA);

        assertThat(medicoLivre).isNull();
    }

    @Test
    @DisplayName("Deveria devolver Medico quando ele estiver Disponivel na Data")
    void escolherMedicoPorEspecialidadeEDataHoraCenario2() {

        var proximaSegundaAsDez = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atTime(10, 0);

        var medico = cadastrarMedico("tanto faz", "tanto.faz@gmail.com", "123456", Especialidade.CARDIOLOGIA);

        var medicoLivre = repository.escolherMedicoPorEspecialidadeEDataHora(proximaSegundaAsDez.toLocalDate(),
                proximaSegundaAsDez.toLocalTime(), Especialidade.CARDIOLOGIA);

        assertThat(medicoLivre).isEqualTo(medico);
    }

    private void cadastrarConsulta(Medico medico, Paciente paciente, LocalDateTime data) {
        em.persist(new Consulta(null, data.toLocalDate(), data.toLocalTime(),
                true, medico, paciente, null));
    }

    private Medico cadastrarMedico(String nome, String email, String crm, Especialidade especialidade) {
        var medico = new Medico(dadosMedico(nome, email, crm, especialidade));
        em.persist(medico);
        return medico;
    }

    private Paciente cadastrarPaciente(String nome, String email, String cpf) {
        var paciente = new Paciente(dadosPaciente(nome, email, cpf));
        em.persist(paciente);
        return paciente;
    }

    private CadastroMedicoDTO dadosMedico(String nome, String email, String crm, Especialidade especialidade) {
        return new CadastroMedicoDTO(
                nome,
                email,
                crm,
                "61999999999",
                especialidade,
                dadosEndereco(),
                LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atTime(10, 0)
        );
    }

    private CadastrarPacienteDTO dadosPaciente(String nome, String email, String cpf) {
        return new CadastrarPacienteDTO(
                nome,
                email,
                "61999999999",
                cpf,
                dadosEndereco()
        );
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