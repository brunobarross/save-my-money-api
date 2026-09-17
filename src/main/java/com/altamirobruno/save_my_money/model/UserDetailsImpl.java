package com.altamirobruno.save_my_money.model;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class UserDetailsImpl implements UserDetails {

    private  UUID id;
    private final String username;
    private final String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(User user){
        this.id = user.getUserId();
        this.username = user.getName();
        this.password = user.getPassword();
        this.authorities = List.of(new SimpleGrantedAuthority(user.getName()));
    }

    public UUID getId(){
        return id;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

}
