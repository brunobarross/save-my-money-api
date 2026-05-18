package com.altamirobruno.save_my_money.controller;


import com.altamirobruno.save_my_money.dto.LoginDTO;
import com.altamirobruno.save_my_money.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/authenticate")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping
    public String login(@RequestBody LoginDTO loginDTO) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.username(), loginDTO.password()
                    ));
            return authService.login(authentication);
        }
        catch (AuthenticationException error) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.info("Autenticação falhou para o usuário " + loginDTO.username() + ": " + error.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, error.getMessage());

        }


    }



}
