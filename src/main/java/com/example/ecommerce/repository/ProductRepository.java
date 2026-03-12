package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"category", "images"})
    List<Product> findByDeletedFalse();

    @EntityGraph(attributePaths = {"category", "images"})
    Optional<Product> findWithImagesById(Long id);
}