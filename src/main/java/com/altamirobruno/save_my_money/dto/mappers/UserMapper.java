package com.altamirobruno.save_my_money.dto.mappers;

import com.altamirobruno.save_my_money.dto.UserDTO;
import com.altamirobruno.save_my_money.enums.RoleName;
import com.altamirobruno.save_my_money.model.Role;
import com.altamirobruno.save_my_money.model.User;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
public class UserMapper {
    public UserDTO toDTO(User user) {
        return new UserDTO(user.getUserId(), user.getUsername(), user.getPassword(), user.getRole());

    }

    public User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        User user = new User();

        user.setUserId(userDTO.id());
        user.setName(userDTO.name());
        user.setPassword(userDTO.password());
        user.setRole(userDTO.role());
        return user;
    }

}
