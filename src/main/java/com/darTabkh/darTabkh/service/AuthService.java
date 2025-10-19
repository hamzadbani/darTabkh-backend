package com.darTabkh.darTabkh.service;

import com.darTabkh.darTabkh.config.JwtUtil;
import com.darTabkh.darTabkh.dto.AuthResponse;
import com.darTabkh.darTabkh.dto.LoginRequest;
import com.darTabkh.darTabkh.dto.RegisterRequest;
import com.darTabkh.darTabkh.entity.Role;
import com.darTabkh.darTabkh.entity.User;
import com.darTabkh.darTabkh.exception.AuthenticationException;
import com.darTabkh.darTabkh.exception.ResourceNotFoundException;
import com.darTabkh.darTabkh.mapper.AuthMapper;
import com.darTabkh.darTabkh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final AuthMapper authMapper;

    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthenticationException("Email is already taken!");
        }
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AuthenticationException("Username is already taken!");
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .city(request.getCity())
                .build();

        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user);

        AuthResponse response = authMapper.toAuthResponse(user);
        response.setToken(token);
        response.setType("Bearer");
        
        return response;
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        String token = jwtUtil.generateToken(user);

        AuthResponse response = authMapper.toAuthResponse(user);
        response.setToken(token);
        response.setType("Bearer");
        
        return response;
    }
}
