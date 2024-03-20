package com.dm.debtease.config;

import com.dm.debtease.model.DebtCase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@SuppressWarnings("unused")
public class KafkaConsumerConfig {
    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServer;
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Bean
    public ProducerFactory<String, DebtCase> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, DebtCase> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, DebtCase> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(DebtCase.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DebtCase> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DebtCase> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
