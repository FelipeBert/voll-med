package org.FelipeBert.api.model;

import jakarta.persistence.*;
import lombok.*;
import org.FelipeBert.api.dto.CadastroMedicoDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Medico")
@Table(name = "medicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
@Setter
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String email;

    private String crm;

    private String telefone;

    @Enumerated(EnumType.STRING)
    private Especialidade especialidade;

    @Embedded
    private Endereco endereco;

    private boolean ativo;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Consulta> consultas = new ArrayList<>();

    public Medico(CadastroMedicoDTO dados) {
        this.nome = dados.nome();
        this.email = dados.email();
        this.crm = dados.crm();
        this.telefone = dados.telefone();
        this.especialidade = dados.especialidade();
        this.endereco = new Endereco(dados.endereco());
        this.ativo = true;
        this.dataHora = dados.hora();
    }
}