package com.example.office.config;


import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaProducerConfig {

    @Value(value = "${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapAddress;
    private final MeterRegistry meterRegistry;

    @Autowired
    public KafkaProducerConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }


    @Bean
    public ProducerFactory<String,String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            bootstrapAddress);
        configProps.put(
            ProducerConfig.BATCH_SIZE_CONFIG,
            1500);
        configProps.put(
            ProducerConfig.LINGER_MS_CONFIG,
            100);
        configProps.put(
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class);
        configProps.put(
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            JsonSerializer.class);
        configProps.put(
            ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
            true);
        configProps.put(
            ProducerConfig.TRANSACTIONAL_ID_CONFIG,
            "my-transactional-id");
        DefaultKafkaProducerFactory<String, String> factory = new DefaultKafkaProducerFactory<>(configProps);
        factory.setTransactionIdPrefix("tx-");
        //factory.addListener(new MicrometerProducerListener<>(meterRegistry));
        return factory;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        KafkaTemplate<String, String> template = new KafkaTemplate<>(producerFactory());
        template.setObservationEnabled(true);
        //template.setObservationConvention(KafkaListenerObservation.DefaultKafkaListenerObservationConvention);
        return template;
    }

    @Bean
    public NewTopic producerTopic() {
        return TopicBuilder.name("account-transfers")
            .partitions(3)
            .replicas(1)
            .config(TopicConfig.RETENTION_MS_CONFIG, "300000")
            .build();
    }

    @Bean
    public KafkaTransactionManager<String, String> transactionManager() {
        return new KafkaTransactionManager<>(producerFactory());
    }
}
