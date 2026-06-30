package com.higienizaplus.backend.service;

import com.higienizaplus.backend.dto.LoginRequest;
import com.higienizaplus.backend.dto.LoginResponse;
import com.higienizaplus.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            String token = jwtTokenProvider.generateToken(authentication.getName());
            return LoginResponse.of(token, authentication.getName(), jwtTokenProvider.getExpirationMs());

        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new BadCredentialsException("Usuário ou senha inválidos");
        }
    }
}
