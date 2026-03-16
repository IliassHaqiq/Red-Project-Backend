package com.example.ecommerce.unit.service;

import com.example.ecommerce.dto.OrderItemRequest;
import com.example.ecommerce.dto.OrderRequest;
import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.repository.CustomerOrderRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.NotificationService;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CustomerOrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductService productService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldRefuseOrderWhenQuantityExceedsStock() {
        User customer = User.builder()
                .id(1L)
                .email("client@shop.com")
                .fullName("Client Demo")
                .password("x")
                .roles(Set.of())
                .build();

        Category category = Category.builder()
                .id(1L)
                .name("Electronique")
                .build();

        Product product = Product.builder()
                .id(10L)
                .name("Produit test")
                .price(new BigDecimal("100.00"))
                .stock(1)
                .category(category)
                .build();

        when(userRepository.findByEmail("client@shop.com")).thenReturn(Optional.of(customer));
        when(productService.getEntityVisibleById(10L)).thenReturn(product);

        OrderRequest request = new OrderRequest(
                java.util.List.of(new OrderItemRequest(10L, 2))
        );

        assertThrows(BusinessException.class, () ->
                orderService.createOrder("client@shop.com", request)
        );
    }

    @Test
    void shouldCreateValidatedOrderAndDecreaseStock() {
        User customer = User.builder()
                .id(1L)
                .email("client@shop.com")
                .fullName("Client Demo")
                .password("x")
                .roles(Set.of())
                .build();

        Category category = Category.builder()
                .id(1L)
                .name("Electronique")
                .build();

        Product product = Product.builder()
                .id(10L)
                .name("Produit test")
                .price(new BigDecimal("100.00"))
                .stock(5)
                .category(category)
                .build();

        when(userRepository.findByEmail("client@shop.com")).thenReturn(Optional.of(customer));
        when(productService.getEntityVisibleById(10L)).thenReturn(product);
        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        OrderRequest request = new OrderRequest(
                java.util.List.of(new OrderItemRequest(10L, 2))
        );

        OrderResponse response = orderService.createOrder("client@shop.com", request);

        assertNotNull(response);
        assertEquals("client@shop.com", response.customerEmail());
        assertEquals("VALIDEE", response.status());
        assertEquals(new BigDecimal("200.00"), response.totalAmount());
        assertEquals(3, product.getStock());
        assertEquals(1, response.items().size());
    }
}