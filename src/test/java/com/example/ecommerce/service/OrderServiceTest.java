package com.example.ecommerce.service;

import com.example.ecommerce.dto.OrderItemRequest;
import com.example.ecommerce.dto.OrderRequest;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.repository.CustomerOrderRepository;
import com.example.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock private CustomerOrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductService productService;
    @Mock private NotificationService notificationService;
    @InjectMocks private OrderService orderService;

    @Test
    void shouldRefuseOrderWhenQuantityExceedsStock() {
        User customer = User.builder().id(1L).email("client@shop.com").fullName("Client")
                .password("x").roles(Set.of()).build();
        Category category = Category.builder().id(1L).name("Electronique").build();
        Product product = Product.builder().id(10L).name("Produit test")
                .price(new BigDecimal("100.00")).stock(1).category(category).build();

        when(userRepository.findByEmail("client@shop.com")).thenReturn(Optional.of(customer));
        when(productService.getEntityVisibleById(10L)).thenReturn(product);

        OrderRequest request = new OrderRequest(List.of(new OrderItemRequest(10L, 2)));

        assertThrows(BusinessException.class, () -> orderService.createOrder("client@shop.com", request));
    }
}
