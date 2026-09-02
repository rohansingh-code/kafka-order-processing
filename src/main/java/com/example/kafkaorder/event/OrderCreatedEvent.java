package com.example.kafkaorder.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    private String eventType;
    private String orderId;
    private String userId;
    private String product;
    private Integer quantity;
    private BigDecimal amount;
    private LocalDateTime timestamp;
}
