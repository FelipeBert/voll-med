package org.FelipeBert.api.model;

import jakarta.persistence.*;
import lombok.*;
import org.FelipeBert.api.dto.AtualizarMedicoDTO;
import org.FelipeBert.api.dto.CadastroMedicoDTO;

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

    public Medico(CadastroMedicoDTO dados) {
        this.nome = dados.nome();
        this.email = dados.email();
        this.crm = dados.crm();
        this.telefone = dados.telefone();
        this.especialidade = dados.especialidade();
        this.endereco = new Endereco(dados.endereco());
        this.ativo = true;
    }

    public void atualizarDados(AtualizarMedicoDTO dados) {
        if(dados.nome() != null && !dados.nome().isEmpty()){
            this.nome = dados.nome();
        }
        if(dados.telefone() != null && !dados.telefone().isEmpty()){
            this.telefone = dados.telefone();
        }
        if(dados.endereco() != null){
            this.endereco.atualizarDados(dados.endereco());
        }
    }

    public void desativarUsuario() {
        this.ativo = false;
    }
}