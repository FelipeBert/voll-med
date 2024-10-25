package org.FelipeBert.api.repository;

import org.FelipeBert.api.model.Consulta;
import org.FelipeBert.api.model.Medico;
import org.FelipeBert.api.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    boolean existsByMedicoAndDataAndHora(Medico medico, LocalDate localDate, LocalTime localTime);

    boolean existsByPacienteAndData(Paciente paciente, LocalDate localDate);
}
