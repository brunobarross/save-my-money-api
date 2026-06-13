package com.altamirobruno.save_my_money.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class JWTService {
    private final JwtEncoder encoder;

    public JWTService(JwtEncoder encoder) {
        this.encoder = encoder;
    }


    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        long expire = 180000L;
        String scopes = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        var claimns = JwtClaimsSet.builder()
                .issuer("spring-security-jwt")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expire))
                .subject(authentication.getName())
                .claim("roles", scopes)
                .build();

        return encoder.encode(JwtEncoderParameters.from(claimns)).getTokenValue();


    }
}