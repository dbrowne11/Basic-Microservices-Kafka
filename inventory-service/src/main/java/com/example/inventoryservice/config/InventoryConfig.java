package com.example.inventoryservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.messaging.Message;

import java.util.HashMap;

@Configuration
public class InventoryConfig {
    @Value("${spring.kafka.producer.topic.name}")
    private String producerTopic;
    @Value("${spring.kafka.consumer.topic.name}")
    private String consumerTopic;

    @Bean
    public NewTopic producerTopic() {
        return TopicBuilder.name(producerTopic)
                .build();
    }

}
