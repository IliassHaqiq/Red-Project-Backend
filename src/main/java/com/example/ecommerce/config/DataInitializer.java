package com.example.ecommerce.config;

import com.example.ecommerce.entity.*;
import com.example.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Role clientRole = roleRepository.findByName(RoleName.ROLE_CLIENT)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_CLIENT).build()));
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_ADMIN).build()));
        Role adminSupRole = roleRepository.findByName(RoleName.ROLE_ADMINSUP)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_ADMINSUP).build()));

        if (!userRepository.existsByEmail("admin@shop.com")) {
            userRepository.save(User.builder()
                    .fullName("Admin Demo")
                    .email("admin@shop.com")
                    .password(passwordEncoder.encode("Admin123!"))
                    .roles(Set.of(adminRole))
                    .build());
        }
        if (!userRepository.existsByEmail("adminsup@shop.com")) {
            userRepository.save(User.builder()
                    .fullName("Admin Sup")
                    .email("adminsup@shop.com")
                    .password(passwordEncoder.encode("Adminsup123!"))
                    .roles(Set.of(adminSupRole))
                    .build());
        }
        if (!userRepository.existsByEmail("client@shop.com")) {
            userRepository.save(User.builder()
                    .fullName("Client Demo")
                    .email("client@shop.com")
                    .password(passwordEncoder.encode("Client123!"))
                    .roles(Set.of(clientRole))
                    .build());
        }

        if (categoryRepository.count() == 0) {
            Category cat1 = categoryRepository.save(Category.builder().name("Electronique").build());
            Category cat2 = categoryRepository.save(Category.builder().name("Livres").build());
            productRepository.save(Product.builder().name("Clavier mecanique").description("Clavier RGB")
                    .price(new BigDecimal("599.00")).stock(12).category(cat1).build());
            productRepository.save(Product.builder().name("Casque audio").description("Casque sans fil")
                    .price(new BigDecimal("899.00")).stock(8).category(cat1).build());
            productRepository.save(Product.builder().name("Spring Boot Guide").description("Livre pratique")
                    .price(new BigDecimal("249.00")).stock(20).category(cat2).build());
        }
    }
}
