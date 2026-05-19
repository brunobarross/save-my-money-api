package com.altamirobruno.save_my_money.dto;

import com.altamirobruno.save_my_money.enums.RoleName;
import org.springframework.security.core.userdetails.User;

public record LoginResponseDTO(
        User user,
        String accessToken
) {
}
