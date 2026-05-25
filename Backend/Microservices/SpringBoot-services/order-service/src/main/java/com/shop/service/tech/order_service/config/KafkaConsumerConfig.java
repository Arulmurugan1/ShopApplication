package com.shop.service.tech.order_service.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import com.shop.service.tech.order_service.events.OrderPlacedEvent;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableKafka
@Slf4j
public class KafkaConsumerConfig {
    
    @Bean
    public ConsumerFactory<String, OrderPlacedEvent> consumerFactory() {

        log.info("Setting up Kafka Consumer Factory for OrderPlacedEvent");

        JsonDeserializer<OrderPlacedEvent> deserializer = new JsonDeserializer<>(OrderPlacedEvent.class);

        deserializer.addTrustedPackages("com.shop.service.tech.order_service.events");
        deserializer.setUseTypeMapperForKey(false);
        deserializer.setRemoveTypeHeaders(true);

        log.info("Kafka Consumer Factory configured with deserializer for OrderPlacedEvent");

        Map<String, Object> props = new HashMap<>();

        log.info("Kafka Consumer properties set: BOOTSTRAP_SERVERS_CONFIG=localhost:9092, GROUP_ID_CONFIG=order-group");

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer.getClass());
        

        log.info("Kafka Consumer Factory initialized with properties");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new ErrorHandlingDeserializer<>(deserializer));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent> kafkaListenerContainerFactory() {

        log.info("Setting up Kafka Listener Container Factory for OrderPlacedEvent");

        ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        factory.setCommonErrorHandler(new DefaultErrorHandler((record, exception) -> {
            log.error("Kafka record processing failed for topic={} partition={} offset={} key={}. Error: {}",
                    record.topic(), record.partition(), record.offset(), record.key(), exception.getMessage(), exception);
        }, new FixedBackOff(0L, 0L)));

        log.info("Kafka Listener Container Factory initialized with consumer factory");
        return factory;
    }
}
