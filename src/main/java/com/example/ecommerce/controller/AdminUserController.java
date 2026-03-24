package com.example.ecommerce.controller;

import com.example.ecommerce.dto.UserSummaryResponse;
import com.example.ecommerce.dto.UpdateUserRoleRequest;
import com.example.ecommerce.dto.CreateAdminUserRequest;
import com.example.ecommerce.dto.UpdateAdminUserRequest;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.RoleName;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/adminsup/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINSUP')")
public class AdminUserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public List<UserSummaryResponse> getAllUsers() {
        // Only manage admin/adminsup accounts, not clients
        return userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().noneMatch(r -> r.getName() == RoleName.ROLE_CLIENT))
                .map(UserSummaryResponse::fromEntity)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserSummaryResponse createAdminUser(@Valid @RequestBody CreateAdminUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email déjà utilisé");
        }

        RoleName roleName = parseAdminRole(request.role());
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BusinessException("Rôle introuvable"));

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.of(role))
                .build();

        user = userRepository.save(user);
        return UserSummaryResponse.fromEntity(user);
    }

    @PutMapping("/{id}")
    public UserSummaryResponse updateAdminUser(@PathVariable Long id, @Valid @RequestBody UpdateAdminUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        ensureNotClient(user);

        if (request.email() != null && !request.email().isBlank() && !request.email().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new BusinessException("Email déjà utilisé");
            }
            user.setEmail(request.email());
        }

        if (request.fullName() != null && !request.fullName().isBlank()) {
            user.setFullName(request.fullName());
        }

        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        if (request.role() != null && !request.role().isBlank()) {
            RoleName roleName = parseAdminRole(request.role());
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new BusinessException("Rôle introuvable"));
            user.setRoles(Set.of(role));
        }

        user = userRepository.save(user);
        return UserSummaryResponse.fromEntity(user);
    }

    @PatchMapping("/{id}/role")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserRole(@PathVariable Long id, @Valid @RequestBody UpdateUserRoleRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        ensureNotClient(user);
        RoleName roleName = parseAdminRole(request.role());

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BusinessException("Rôle introuvable"));

        user.setRoles(Set.of(role));
        userRepository.save(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteAdminUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        ensureNotClient(user);

        // Ensure join table rows are removed before deleting user (MySQL FK safety)
        user.getRoles().clear();
        userRepository.save(user);
        userRepository.delete(user);
    }

    private void ensureNotClient(User user) {
        boolean isClient = user.getRoles().stream().anyMatch(r -> r.getName() == RoleName.ROLE_CLIENT);
        if (isClient) throw new BusinessException("Les clients ne sont pas gérés ici");
    }

    private RoleName parseAdminRole(String role) {
        if (role == null || role.isBlank()) {
            throw new BusinessException("Rôle invalide");
        }

        String normalized = role.trim().toUpperCase();

        // Accepter les libellés envoyés par le front
        if (normalized.equals("ADMIN")) {
            normalized = "ROLE_ADMIN";
        } else if (normalized.equals("ADMINSUP") || normalized.equals("ADMIN SUP") || normalized.equals("ADMIN_SUP")) {
            normalized = "ROLE_ADMINSUP";
        }

        RoleName roleName;
        try {
            roleName = RoleName.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Rôle invalide");
        }

        if (roleName == RoleName.ROLE_CLIENT) {
            throw new BusinessException("Les clients ne sont pas gérés ici");
        }

        return roleName;
    }
}

