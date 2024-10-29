package org.FelipeBert.api.controller;

import jakarta.validation.Valid;
import org.FelipeBert.api.domain.dto.in.DadosAutenticacaoDTO;
import org.FelipeBert.api.domain.dto.out.TokenDadosDTO;
import org.FelipeBert.api.infra.security.TokenService;
import org.FelipeBert.api.domain.model.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {

    private AuthenticationManager manager;

    private TokenService tokenService;

    public AutenticacaoController(AuthenticationManager manager, TokenService tokenService) {
        this.manager = manager;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity realizarLogin(@RequestBody @Valid DadosAutenticacaoDTO dados){
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
        var authentication = manager.authenticate(authenticationToken);

        var tokenJwt = tokenService.gerarToken((Usuario) authentication.getPrincipal());

        return ResponseEntity.ok(new TokenDadosDTO(tokenJwt));
    }
}