package org.FelipeBert.api.infra.repository;

import org.FelipeBert.api.domain.model.Especialidade;
import org.FelipeBert.api.domain.model.Medico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
    Page<Medico> findAllByAtivoTrue(Pageable paginacao);

    @Query("""
            select m from Medico m
             where m.ativo = true
             and m.especialidade = :especialidade
             and m.id not in (
                select c.medico.id from Consulta c where
                c.data = :data
                and c.hora = :hora
             )
             order by rand() limit 1
            """)
    Medico escolherMedicoPorEspecialidadeEDataHora(LocalDate data, LocalTime hora, Especialidade especialidade);

    @Query("select m.ativo from Medico m where m.id = :id")
    boolean findAtivoById(Long id);
}
