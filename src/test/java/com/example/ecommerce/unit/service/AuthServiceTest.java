package com.example.ecommerce.unit.service;

import com.example.ecommerce.dto.AuthResponse;
import com.example.ecommerce.dto.LoginRequest;
import com.example.ecommerce.dto.RegisterRequest;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("register() doit créer un nouvel utilisateur client et retourner un token JWT")
    void shouldRegisterUserSuccessfully() {
        String email = "newuser@test.com";

        assertFalse(userRepository.existsByEmail(email));

        RegisterRequest request = new RegisterRequest(
                "New User",
                email,
                "Password123!"
        );

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertNotNull(response.token());
        assertEquals(email, response.email());
        assertTrue(response.roles().contains("ROLE_CLIENT"));

        assertTrue(userRepository.existsByEmail(email));
    }

    @Test
    @DisplayName("register() doit lever une BusinessException si l'email existe deja")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest(
                "Client Demo",
                "client@shop.com",
                "Client123!"
        );

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> authService.register(request)
        );

        assertEquals("Cet email existe deja", exception.getMessage());
    }

    @Test
    @DisplayName("login() doit retourner un token JWT avec un utilisateur existant")
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest(
                "client@shop.com",
                "Client123!"
        );

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertNotNull(response.token());
        assertEquals("client@shop.com", response.email());
        assertTrue(response.roles().contains("ROLE_CLIENT"));
    }

    @Test
    @DisplayName("login() doit lever une BadCredentialsException si le mot de passe est incorrect")
    void shouldFailLoginWhenPasswordIsIncorrect() {
        LoginRequest request = new LoginRequest(
                "client@shop.com",
                "WrongPassword"
        );

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );
    }
}