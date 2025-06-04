package com.example.consumer.listener;

import com.example.consumer.model.Transfer;
import com.example.consumer.listener.processor.AccountTransferProcessor;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class MessageListener {

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
    public void consumeTransferEvent(List<ConsumerRecord<String, Transfer>> records) {
        Integer partitionNumber = records.get(0).partition();
        log.info("Logger _{}_ received {} messages / {} records", partitionNumber, records.size(), records.size());
        receivedCounter.increment(records.size());
        for (ConsumerRecord<String, Transfer> record : records) {
            log.info("Logger _{}_ received key {}: | Payload: {} | Record: {}", partitionNumber, record.key(),
                record.value(), record);
            try {
                accountTransferProcessor.processTransfer(record.value());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
