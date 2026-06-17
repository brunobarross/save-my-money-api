package com.altamirobruno.save_my_money.service;

import com.altamirobruno.save_my_money.dto.UserDTO;
import com.altamirobruno.save_my_money.dto.mappers.UserMapper;
import com.altamirobruno.save_my_money.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDTO getUserLogged(@AuthenticationPrincipal Jwt jwt){
        String username = jwt.getSubject();
        return userRepository.findUserByName(username)
                .map(userMapper::toDTO)
                .orElseThrow(()-> new UsernameNotFoundException(username));
    }
}
