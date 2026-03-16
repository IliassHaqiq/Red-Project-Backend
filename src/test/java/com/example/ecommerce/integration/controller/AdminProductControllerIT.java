package com.example.ecommerce.integration.controller;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @WithMockUser(username = "admin@shop.com", roles = {"ADMIN"})
    void createProduct_shouldSucceedForAdmin() throws Exception {
        Category anyCategory = categoryRepository.findAll().stream().findFirst().orElseThrow();
        assertThat(anyCategory.getId()).isNotNull();

        String body = """
                {
                  "name": "Produit IT",
                  "description": "Desc",
                  "price": 123.45,
                  "stock": 5,
                  "categoryId": %d,
                  "images": []
                }
                """.formatted(anyCategory.getId());

        mockMvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Produit IT"))
                .andExpect(jsonPath("$.price").value(BigDecimal.valueOf(123.45)));
    }
}

