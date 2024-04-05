package com.example.inventoryservice.service;

import com.example.inventoryservice.entity.Product;
import com.example.inventoryservice.payload.OrderEvent;
import com.example.inventoryservice.repository.ProductRepository;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {
private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Autowired
    private NewTopic producerTopic;

    @KafkaListener(topics="${spring.kafka.consumer.topic.name}", groupId="${spring.kafka.consumer.group-id}")
    public void consume(@Payload OrderEvent orderEvent) {
        LOGGER.info(String.format("order event received by inventory service: %s", orderEvent));
        // Find product
        Product product = productRepository.findById(orderEvent.getOrderRef().getProductId())
                .orElse(null);
        // Handle product not found
        if (product == null) {
            LOGGER.warn(String.format("Order product not found with:  %s", orderEvent.toString()));
            return;
        }
        // convenience vars
        Long orderQuantity = orderEvent.getOrderRef().getQuantity();
        Long productQuantity = product.getQuantity();
        Long remaining  = productQuantity - orderQuantity;
        // Handle order with sufficient inventory
        if (remaining > 0) {
            product.setQuantity(remaining);
            inventoryService.updateProduct(product);
            approveOrder(orderEvent);
        }
        // Handle insufficient inventory
        else {
            refuseOrder(orderEvent);
            LOGGER.warn(String.format("Inventory Insufficient (quantity: %s) for order of size %s", productQuantity, orderQuantity));
        }
    }

    public void approveOrder(OrderEvent orderEvent) {
        orderEvent.setStatus("PLACED");
        sendMessage(orderEvent);

    }

    public void refuseOrder(OrderEvent orderEvent) {
        orderEvent.setStatus("CANNOT PLACE ORDER");
        sendMessage(orderEvent);
    }

    public void sendMessage(OrderEvent orderEvent) {
        Message<OrderEvent> message = MessageBuilder
                .withPayload(orderEvent)
                .setHeader(KafkaHeaders.TOPIC, producerTopic.name())
                .build();

        kafkaTemplate.send(message);
    }
}
