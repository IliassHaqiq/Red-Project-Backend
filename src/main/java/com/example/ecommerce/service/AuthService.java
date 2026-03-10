package com.example.ecommerce.service;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.entity.RoleName;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Cet email existe deja");
        }

        com.example.ecommerce.entity.User user = com.example.ecommerce.entity.User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.of(roleRepository.findByName(RoleName.ROLE_CLIENT)
                        .orElseThrow(() -> new BusinessException("Role client introuvable"))))
                .build();
        userRepository.save(user);
        return buildResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));
        return buildResponse(user);
    }

    private AuthResponse buildResponse(com.example.ecommerce.entity.User user) {
        var principal = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRoles().stream().map(r -> r.getName().name()).toArray(String[]::new))
                .build();
        String token = jwtService.generateToken(principal, Map.of("roles",
                user.getRoles().stream().map(r -> r.getName().name()).toList()));
        return new AuthResponse(token, user.getEmail(),
                user.getRoles().stream().map(r -> r.getName().name()).collect(java.util.stream.Collectors.toSet()));
    }
}
