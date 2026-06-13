package com.altamirobruno.save_my_money.dto.mappers;

import com.altamirobruno.save_my_money.dto.UserDTO;
import com.altamirobruno.save_my_money.model.User;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class UserMapper {
    public UserDTO toDTO(User user) {
        return new UserDTO(user.getUserId(), user.getUsername());

    }

}
