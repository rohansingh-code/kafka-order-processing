package com.example.kafkaorder.service;

import com.example.kafkaorder.dto.OrderRequest;
import com.example.kafkaorder.model.Order;
import com.example.kafkaorder.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final com.example.kafkaorder.producer.KafkaOrderProducer kafkaOrderProducer;

    @Transactional
    public Order createOrder(OrderRequest request) {
        log.info("Creating order for user: {}", request.getUserId());
        
        Order order = Order.builder()
                .userId(request.getUserId())
                .product(request.getProduct())
                .quantity(request.getQuantity())
                .amount(request.getAmount())
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);
        
        log.info("Order saved successfully with ID: {}", savedOrder.getOrderId());
        
        com.example.kafkaorder.event.OrderCreatedEvent event = com.example.kafkaorder.event.OrderCreatedEvent.builder()
                .eventType("ORDER_CREATED")
                .orderId(savedOrder.getOrderId())
                .userId(savedOrder.getUserId())
                .product(savedOrder.getProduct())
                .quantity(savedOrder.getQuantity())
                .amount(savedOrder.getAmount())
                .timestamp(savedOrder.getCreatedAt())
                .build();
                
        kafkaOrderProducer.sendOrderCreatedEvent(event);

        return savedOrder;
    }

    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
