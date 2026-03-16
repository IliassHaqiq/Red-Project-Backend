package com.example.ecommerce.unit.service;

import com.example.ecommerce.dto.ProductResponse;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.CategoryService;
import com.example.ecommerce.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldReturnOnlyVisibleProducts() {
        Category category = Category.builder()
                .id(1L)
                .name("Electronique")
                .build();

        Product visible = Product.builder()
                .id(1L)
                .name("Clavier mecanique")
                .description("Clavier RGB")
                .price(new BigDecimal("599.00"))
                .stock(12)
                .deleted(false)
                .category(category)
                .build();

        when(productRepository.findByDeletedFalse()).thenReturn(List.of(visible));

        List<ProductResponse> result = productService.getVisibleProducts();

        assertEquals(1, result.size());
        assertEquals("Clavier mecanique", result.get(0).name());
        assertEquals("Electronique", result.get(0).category());
    }
}