package com.altamirobruno.save_my_money.service;

import com.altamirobruno.save_my_money.dto.LoginRequestDTO;
import com.altamirobruno.save_my_money.dto.LoginResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.logging.Logger;

@Service
public class AuthService {
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(JWTService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }


    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.username(), loginRequestDTO.password()));

            return new LoginResponseDTO(
                    (User) authentication.getPrincipal(),
                    jwtService.generateToken(authentication)
            );
        } catch (AuthenticationException error) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.info("Autenticação falhou para o usuário " + loginRequestDTO.username() + ": " + error.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou senha incorretos!");
        }
    }
}

