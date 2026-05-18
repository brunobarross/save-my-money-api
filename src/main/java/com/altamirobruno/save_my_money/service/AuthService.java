package com.altamirobruno.save_my_money.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final JWTService jwtService;
    public AuthService(JWTService jwtService) {
        this.jwtService = jwtService;
    }


    public String login(Authentication authentication) {
        return jwtService.generateToken(authentication);
    }
}

