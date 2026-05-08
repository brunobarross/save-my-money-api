package com.altamirobruno.save_my_money.configs.security;

import com.altamirobruno.save_my_money.enums.RoleName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(HttpMethod.GET,"/api/wallets/**").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/wallets").permitAll()
//                        .requestMatchers(HttpMethod.DELETE, "/api/wallets").hasRole(RoleName.ADMIN.toString())
//                        .requestMatchers(HttpMethod.GET, "/api/transactions/**").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/transactions").permitAll()
//                        .requestMatchers(HttpMethod.DELETE, "/api/transactions/").hasRole(RoleName.ADMIN.toString())
                        .anyRequest().authenticated());

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
