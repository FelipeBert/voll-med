package org.FelipeBert.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity(name = "Consulta")
@Table(name = "consultas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate data;

    private LocalTime hora;

    private boolean marcada;

    @ManyToOne
    private Medico medico;

    @ManyToOne
    private Paciente paciente;

    @Column(name = "motivo_cancelamento")
    private String motivoCancelamento;
}
