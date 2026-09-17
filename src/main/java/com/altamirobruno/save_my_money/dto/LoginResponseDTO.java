package com.altamirobruno.save_my_money.dto;

import org.springframework.security.core.userdetails.User;

public record LoginResponseDTO(
        User user,
        String accessToken
) {
}
