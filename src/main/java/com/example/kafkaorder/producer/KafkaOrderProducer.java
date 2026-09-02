package com.example.kafkaorder.producer;

import com.example.kafkaorder.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaOrderProducer {

    private static final String TOPIC = "orders";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Publishing OrderCreatedEvent for orderId: {}", event.getOrderId());
        kafkaTemplate.send(TOPIC, event.getOrderId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Successfully published OrderCreatedEvent for orderId: {}", event.getOrderId());
                    } else {
                        log.error("Failed to publish OrderCreatedEvent for orderId: {}", event.getOrderId(), ex);
                    }
                });
    }
}
