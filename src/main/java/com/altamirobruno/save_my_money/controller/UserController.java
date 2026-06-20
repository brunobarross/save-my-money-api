package com.altamirobruno.save_my_money.controller;

import com.altamirobruno.save_my_money.dto.UserDTO;
import com.altamirobruno.save_my_money.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public List<UserDTO> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/me")
    public UserDTO getLoggedInUser(@AuthenticationPrincipal Jwt jwt) {
        return userService.getUserLogged(jwt);
    }

    @PostMapping
    public UserDTO createUser(@NotNull @RequestBody @Valid UserDTO userDTO) {
        return userService.createUser(userDTO);
    }


}
