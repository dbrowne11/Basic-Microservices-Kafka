package com.example.order.service.service;

import com.example.order.service.entity.Order;
import com.example.order.service.entity.OrderEvent;
import com.example.order.service.repository.OrderEventRepository;
import com.example.order.service.repository.OrderRepository;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderProducer {

    @Autowired
    private NewTopic topic;
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderProducer.class);

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Autowired
    private OrderEventRepository orderEventRepository;
    @Autowired
    private OrderRepository orderRepository;

    public void sendMessage(OrderEvent orderEvent) {
        LOGGER.info(String.format("Order event sent: %s", orderEvent));
        orderEventRepository.save(orderEvent);
        Message<OrderEvent> message = MessageBuilder
                .withPayload(orderEvent)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();
        kafkaTemplate.send(message);
    }

    @KafkaListener(topics= "${spring.kafka.consumer.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void receiveInventoryMessage(OrderEvent orderEvent) {
        LOGGER.info(String.format("Received OrderEvent: %s", orderEvent.toString()));
        updateStatus(orderEvent, orderEvent.getStatus());
    }

    public List<OrderEvent> getAllOrderEvents() {
        return orderEventRepository.findAll();
    }

    public OrderEvent getOrderById(String id) {
        return orderEventRepository.findById(id)
                .orElse(null);
    }

    public OrderEvent updateStatus(OrderEvent event, String status) {
        OrderEvent currentRef = getOrderById(event.getEventId());
        currentRef.setStatus(status);
        return orderEventRepository.save(currentRef);
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }
}
