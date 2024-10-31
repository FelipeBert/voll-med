package org.FelipeBert.api.infra.repository;

import org.FelipeBert.api.domain.model.Consulta;
import org.FelipeBert.api.domain.model.Medico;
import org.FelipeBert.api.domain.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    boolean existsByMedicoIdAndDataAndHora(Long id, LocalDate localDate, LocalTime localTime);

    boolean existsByPacienteAndData(Paciente paciente, LocalDate localDate);

    boolean existsByPacienteIdAndData(Long aLong, LocalDate localDate);
}
