package com.example.consumer.listener;

import com.example.consumer.model.Transfer;
import com.example.consumer.listener.processor.AccountTransferProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Component
public class MessageListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AccountTransferProcessor accountTransferProcessor;
    private final Counter receivedCounter;

    @Autowired
    public MessageListener(MeterRegistry registry, AccountTransferProcessor processor) {
        receivedCounter = Counter.builder("transactions_received_total").
            tag("version", "v1").
            description("Transactions Received Count").
            register(registry);
        accountTransferProcessor = processor;
    }
    @KafkaListener(topics = "account-transfers", concurrency = "3", clientIdPrefix = "transfer-consumer",
        groupId = "transfer-group", containerFactory = "kafkaListenerContainerFactory", batch = "true")
    @Observed
    public void consumeTransferEvent(List<ConsumerRecord<String, String>> records) {
        Integer partitionNumber = records.get(0).partition();
        log.info("Logger _{}_ received {} messages / {} records", partitionNumber, records.size(), records.size());
        receivedCounter.increment(records.size());
        for (ConsumerRecord<String, String> record : records) {
            log.info("Logger _{}_ received key {}: Type [{}] | Payload: {} | Record: {}", partitionNumber, record.key(),
                typeIdHeader(record.headers()), record.value(), record);
            try {
                accountTransferProcessor.processTransfer(objectMapper.readValue(objectMapper.readTree(record.value()).asText(), Transfer.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String typeIdHeader(Headers headers) {
        return StreamSupport.stream(headers.spliterator(), false)
            .filter(header -> header.key().equals("__TypeId__"))
            .findFirst().map(header -> new String(header.value())).orElse("N/A");
    }
}
