package com.example.office.service;

import com.example.office.model.Transfer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountTransferEventServiceImpl implements AccountTransferEventService {

    private final KafkaProducerService kafkaProducerService;
    private final AccountService accountService;


    @Scheduled(fixedRate = 1000)
    @Override
    public void generateTransfers() {
        Random rand = new Random();
        List<Transfer> events = new ArrayList<>();
        List<UUID> accounts = accountService.getAccounts();
        if (accounts.isEmpty()) {
            accountService.initAccounts();
        }
        for (int i = 0; i < 5; i ++) {
            Transfer transfer = Transfer.builder()
                .id(UUID.randomUUID())
                .amount(rand.nextDouble(AccountService.MAX_BALANCE))
                .senderId(accounts.get(rand.nextInt(accounts.size())))
                .receiverId(accounts.get(rand.nextInt(accounts.size())))
                .build();
            events.add(transfer);
        }
        kafkaProducerService.sendEvent(events);
    }

}
