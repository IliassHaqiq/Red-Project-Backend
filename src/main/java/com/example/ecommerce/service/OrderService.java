package com.example.ecommerce.service;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CustomerOrderRepository;
import com.example.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CustomerOrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    @Transactional
    public OrderResponse createOrder(String customerEmail, OrderRequest request) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable"));

        CustomerOrder order = CustomerOrder.builder()
                .customer(customer)
                .status(OrderStatus.VALIDEE)
                .totalAmount(BigDecimal.ZERO)
                .build();

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {
            Product product = productService.getEntityVisibleById(itemRequest.productId());
            if (itemRequest.quantity() > product.getStock()) {
                throw new BusinessException("La quantite demandee pour le produit " + product.getName() + " depasse le stock disponible");
            }
            product.setStock(product.getStock() - itemRequest.quantity());
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.quantity())
                    .unitPrice(product.getPrice())
                    .lineTotal(lineTotal)
                    .build();
            items.add(item);
            total = total.add(lineTotal);
        }

        order.setItems(items);
        order.setTotalAmount(total);
        return toResponse(orderRepository.save(order));
    }

    public List<OrderResponse> getMyOrders(String email) {
        return orderRepository.findByCustomerEmail(email).stream().map(this::toResponse).toList();
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, String status) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable"));
        OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
        if (order.getStatus() == OrderStatus.VALIDEE && newStatus == OrderStatus.EN_COURS) {
            throw new BusinessException("Une commande validee est definitive");
        }
        order.setStatus(newStatus);
        return toResponse(orderRepository.save(order));
    }

    private OrderResponse toResponse(CustomerOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomer().getEmail(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(item.getProduct().getId(), item.getProduct().getName(),
                                item.getQuantity(), item.getUnitPrice(), item.getLineTotal()))
                        .toList()
        );
    }
}
