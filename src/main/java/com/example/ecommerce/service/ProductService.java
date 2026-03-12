package com.example.ecommerce.service;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.ProductImage;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public List<ProductResponse> getVisibleProducts() {
        return productRepository.findByDeletedFalse()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse getVisibleProduct(Long id) {
        Product product = productRepository.findWithImagesById(id)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable"));

        return toResponse(product);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .category(categoryService.getById(request.categoryId()))
                .build();

        applyImages(product, request.images());

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findWithImagesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable"));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(categoryService.getById(request.categoryId()));

        product.getImages().clear();
        applyImages(product, request.images());

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void softDelete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable"));

        product.setDeleted(true);
        productRepository.save(product);
    }

    public Product getEntityVisibleById(Long id) {
        return productRepository.findWithImagesById(id)
                .filter(product -> !product.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable"));
    }

    private void applyImages(Product product, List<ProductImageRequest> imageRequests) {
        if (imageRequests == null || imageRequests.isEmpty()) {
            return;
        }

        long primaryCount = imageRequests.stream()
                .filter(img -> Boolean.TRUE.equals(img.primaryImage()))
                .count();

        if (primaryCount > 1) {
            throw new BusinessException("Un seul visuel principal est autorise par produit");
        }

        List<ProductImageRequest> normalized = new ArrayList<>(imageRequests);

        if (primaryCount == 0) {
            ProductImageRequest first = normalized.get(0);
            normalized.set(0, new ProductImageRequest(
                    first.imageUrl(),
                    true,
                    first.displayOrder()
            ));
        }

        for (ProductImageRequest imageRequest : normalized) {
            ProductImage image = ProductImage.builder()
                    .imageUrl(imageRequest.imageUrl())
                    .primaryImage(Boolean.TRUE.equals(imageRequest.primaryImage()))
                    .displayOrder(imageRequest.displayOrder())
                    .product(product)
                    .build();

            product.getImages().add(image);
        }
    }

    private ProductResponse toResponse(Product product) {
        List<ProductImageResponse> images = product.getImages()
                .stream()
                .map(image -> new ProductImageResponse(
                        image.getId(),
                        image.getImageUrl(),
                        image.isPrimaryImage(),
                        image.getDisplayOrder()
                ))
                .toList();

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory().getName(),
                images
        );
    }
}