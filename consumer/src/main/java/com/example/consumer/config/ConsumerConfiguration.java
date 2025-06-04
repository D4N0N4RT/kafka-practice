package com.example.consumer.config;

import com.example.consumer.listener.KafkaErrorHandler;
import com.example.consumer.model.Transfer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableJpaRepositories(basePackages = {"com.example.consumer.dao"})
public class ConsumerConfiguration {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ConsumerFactory<String, Transfer> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            bootstrapAddress);
        configProps.put(
            ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
            "5");
        configProps.put(
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class);
        configProps.put(
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            JsonDeserializer.class);
        /*configProps.put(
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
            "false");
        configProps.put(
            ConsumerConfig.ISOLATION_LEVEL_CONFIG,
            "read_committed");*/
        JsonDeserializer<Transfer> payloadJsonDeserializer = new JsonDeserializer<>();
        Map<String, Object> deserProps = new HashMap<>();
        deserProps.put(
            JsonDeserializer.VALUE_DEFAULT_TYPE, "com.example.consumer.model.Transfer"
        );
        deserProps.put(
            JsonDeserializer.TRUSTED_PACKAGES,
            "*"
        );
        payloadJsonDeserializer.configure(deserProps, false);
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(),
            payloadJsonDeserializer.ignoreTypeHeaders());
    }

    @Bean
    CommonErrorHandler commonErrorHandler() {
        return new KafkaErrorHandler();
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Transfer> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Transfer> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setObservationEnabled(true);
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(commonErrorHandler());
        return factory;
    }
}
