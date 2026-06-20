package com.altamirobruno.save_my_money.service;

import com.altamirobruno.save_my_money.dto.UserDTO;
import com.altamirobruno.save_my_money.dto.mappers.UserMapper;
import com.altamirobruno.save_my_money.model.User;
import com.altamirobruno.save_my_money.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private  final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO getUserLogged(@AuthenticationPrincipal Jwt jwt){
        String username = jwt.getSubject();
        return userRepository.findUserByName(username)
                .map(userMapper::toDTO)
                .orElseThrow(()-> new UsernameNotFoundException(username));
    }

    public List<UserDTO> getAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .toList();
    }

    public UserDTO createUser(UserDTO userDTO) {
        String encoderPassword = passwordEncoder.encode(userDTO.password());
        User newUser = userMapper.toEntity(new UserDTO(userDTO.id(),userDTO.name(), encoderPassword, userDTO.role()));
        return userMapper.toDTO(userRepository.save(newUser));
    }
}
