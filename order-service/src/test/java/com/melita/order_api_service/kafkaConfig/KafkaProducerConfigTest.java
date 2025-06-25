package com.melita.order_api_service.kafkaConfig;

import com.melita.order_api_service.kafkaConfig.event.OrderCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = KafkaProducerConfig.class)
public class KafkaProducerConfigTest {

    @Autowired
    private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @Autowired
    private ProducerFactory<String, OrderCreatedEvent> producerFactory;

    @Test
    void contextLoads() {
        assertNotNull(kafkaTemplate);
        assertNotNull(producerFactory);
    }
}