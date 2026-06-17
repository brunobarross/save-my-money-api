package com.altamirobruno.save_my_money.service;

import com.altamirobruno.save_my_money.model.User;
import com.altamirobruno.save_my_money.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByName(username).orElseThrow(()-> new UsernameNotFoundException("User not found with username" + username));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), true, true, true, true, user.getAuthorities());
    }
}
