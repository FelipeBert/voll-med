package org.FelipeBert.api.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.FelipeBert.api.domain.dto.in.CadastrarPacienteDTO;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "Paciente")
@Table(name = "pacientes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private boolean ativo;

    @Embedded
    private Endereco endereco;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Consulta> consultas = new ArrayList<>();

    public Paciente(CadastrarPacienteDTO dadosPaciente) {
        this.email = dadosPaciente.email();
        this.nome = dadosPaciente.nome();
        this.cpf = dadosPaciente.cpf();
        this.telefone = dadosPaciente.telefone();
        this.endereco = new Endereco(dadosPaciente.endereco());
        this.ativo = true;
    }
}