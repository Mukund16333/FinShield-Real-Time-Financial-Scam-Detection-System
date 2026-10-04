package com.finshield.service;

import com.finshield.dto.request.LoginRequest;
import com.finshield.dto.request.RegisterRequest;
import com.finshield.dto.response.JwtResponse;
import com.finshield.entity.User;
import com.finshield.exception.UnauthorizedActionException;
import com.finshield.repository.UserRepository;
import com.finshield.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UnauthorizedActionException("Email is already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        Authentication authentication = authenticate(request.getEmail(), request.getPassword());
        String token = jwtTokenProvider.generateToken(authentication);
        return new JwtResponse(token, user.getEmail(), user.getRole().name());
    }

    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticate(request.getEmail(), request.getPassword());
        String token = jwtTokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedActionException("Invalid credentials"));

        return new JwtResponse(token, user.getEmail(), user.getRole().name());
    }

    private Authentication authenticate(String email, String password) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
    }
}
