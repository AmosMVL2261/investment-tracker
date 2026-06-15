package com.av.investment_tracker.auth.service;

import com.av.investment_tracker.auth.dto.AuthResponse;
import com.av.investment_tracker.auth.dto.LoginRequest;
import com.av.investment_tracker.auth.dto.RegisterRequest;
import com.av.investment_tracker.security.JwtService;
import com.av.investment_tracker.security.UserPrincipal;
import com.av.investment_tracker.user.model.Role;
import com.av.investment_tracker.user.model.User;
import com.av.investment_tracker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if(userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already in use");
        }
        User user = User.builder()
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(Role.ROLE_USER)
                        .build();

        userRepository.save(user);

        UserPrincipal userPrincipal = new UserPrincipal(user);
        String token = jwtService.generateToken(userPrincipal);

        return new AuthResponse(token);

    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        UserPrincipal userPrincipal = new UserPrincipal(user);
        String token = jwtService.generateToken(userPrincipal);

        return new AuthResponse(token);
    }

}
