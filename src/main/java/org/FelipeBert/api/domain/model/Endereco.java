package org.FelipeBert.api.domain.model;

import jakarta.persistence.Embeddable;
import lombok.*;
import org.FelipeBert.api.domain.dto.in.EnderecoDTO;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {
    private String logradouro;
    private String bairro;
    private String cep;
    private String cidade;
    private String uf;
    private String numero;
    private String complemento;

    public Endereco(EnderecoDTO endereco) {
        this.bairro = endereco.bairro();
        this.logradouro = endereco.logradouro();
        this.cep = endereco.cep();
        this.cidade = endereco.cidade();
        this.uf = endereco.uf();
        this.numero = endereco.numero();
        this.complemento = endereco.complemento();
    }

    public void atualizarDados(EnderecoDTO dados) {
        if(dados.logradouro() != null && !dados.logradouro().isEmpty()){
            this.logradouro = dados.logradouro();
        }
        if(dados.bairro() != null && !dados.bairro().isEmpty()){
            this.bairro = dados.bairro();
        }
        if(dados.cep() != null && !dados.cep().isEmpty()){
            this.cep = dados.cep();
        }
        if(dados.cidade() != null && !dados.cidade().isEmpty()){
            this.cidade = dados.cidade();
        }
        if(dados.uf() != null && !dados.uf().isEmpty()){
            this.uf = dados.uf();
        }
        if(dados.numero() != null && !dados.numero().isEmpty()){
            this.numero = dados.numero();
        }
        if(dados.complemento() != null && !dados.complemento().isEmpty()){
            this.complemento = dados.complemento();
        }
    }
}
