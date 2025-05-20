package com.example.office.service;

import com.example.office.model.Transfer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Transfer> kafkaTemplate;
    private final Counter sentCounter;

    @Autowired
    public KafkaProducerService(MeterRegistry registry, KafkaTemplate<String, Transfer> template) {

        sentCounter = Counter.builder("transactions_sent_total").
            tag("version", "v1").
            description("Transactions Sent Count").
            register(registry);
        kafkaTemplate = template;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Observed
    public void sendEvent(List<Transfer> events) {
        sentCounter.increment(events.size());
        for (Transfer item : events) {
            try {
                log.info("Sent transfer with id = {}", item.getId());
                kafkaTemplate.send("account-transfers", item);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}
