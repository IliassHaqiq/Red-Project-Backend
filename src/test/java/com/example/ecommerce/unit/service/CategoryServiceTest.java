package com.example.ecommerce.unit.service;

import com.example.ecommerce.dto.CategoryRequest;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getAll_shouldReturnAllCategories() {
        Category cat1 = Category.builder().id(1L).name("Electronique").build();
        Category cat2 = Category.builder().id(2L).name("Livres").build();

        when(categoryRepository.findAll()).thenReturn(List.of(cat1, cat2));

        List<Category> result = categoryService.getAll();

        assertEquals(2, result.size());
        assertEquals("Electronique", result.get(0).getName());
        assertEquals("Livres", result.get(1).getName());
    }

    @Test
    void create_shouldPersistCategory() {
        CategoryRequest request = new CategoryRequest("Nouvelles");
        Category saved = Category.builder().id(10L).name("Nouvelles").build();

        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        Category result = categoryService.create(request);

        assertEquals(10L, result.getId());
        assertEquals("Nouvelles", result.getName());
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getById(99L));
    }
}

