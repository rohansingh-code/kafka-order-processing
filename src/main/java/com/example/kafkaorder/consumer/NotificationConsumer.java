package com.example.kafkaorder.consumer;

import com.example.kafkaorder.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationConsumer {

    @KafkaListener(topics = "orders", groupId = "notification-group")
    public void consume(OrderCreatedEvent event) {
        log.info("NotificationConsumer received OrderCreatedEvent for orderId: {}", event.getOrderId());
        
        // Simulating notification logic
        log.info("Notification sent:\nOrder {} created successfully for user {} for {} {}", 
                event.getOrderId(), event.getUserId(), event.getQuantity(), event.getProduct());
    }
}
