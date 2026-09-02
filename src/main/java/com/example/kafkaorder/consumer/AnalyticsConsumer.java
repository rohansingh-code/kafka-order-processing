package com.example.kafkaorder.consumer;

import com.example.kafkaorder.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class AnalyticsConsumer {

    private final AtomicInteger totalOrders = new AtomicInteger(0);
    private final AtomicReference<BigDecimal> totalRevenue = new AtomicReference<>(BigDecimal.ZERO);

    @KafkaListener(topics = "orders", groupId = "analytics-group")
    public void consume(OrderCreatedEvent event) {
        log.info("AnalyticsConsumer received OrderCreatedEvent for orderId: {}", event.getOrderId());
        
        int currentTotalOrders = totalOrders.incrementAndGet();
        BigDecimal currentTotalRevenue = totalRevenue.accumulateAndGet(event.getAmount(), BigDecimal::add);
        
        log.info("--- Analytics Update ---");
        log.info("Total Orders: {}", currentTotalOrders);
        log.info("Total Revenue: {}", currentTotalRevenue);
        log.info("Order details: {} of {} by {}", event.getQuantity(), event.getProduct(), event.getUserId());
        log.info("------------------------");
    }
}
