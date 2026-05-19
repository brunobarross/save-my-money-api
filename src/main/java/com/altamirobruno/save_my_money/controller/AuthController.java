package com.altamirobruno.save_my_money.controller;


import com.altamirobruno.save_my_money.dto.LoginRequestDTO;
import com.altamirobruno.save_my_money.dto.LoginResponseDTO;
import com.altamirobruno.save_my_money.service.AuthService;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authenticate")
public class AuthController {
    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping
    public LoginResponseDTO login(@NotNull @RequestBody LoginRequestDTO loginRequestDTO) {
        return authService.login(loginRequestDTO);
    }
}
