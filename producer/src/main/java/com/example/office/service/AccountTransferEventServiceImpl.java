package com.example.office.service;

import com.example.office.model.Transfer;
import com.example.office.dao.AccountsDAO;
import com.example.office.model.Account;
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
    private final AccountsDAO accountsDAO;
    private List<UUID> accounts = new ArrayList<>();
    private static final Double MAX_BALANCE = 3500000.0;


    //@Transactional
    void initAccounts() {
        log.info("initAccounts():");
        List<Account> accountList = accountsDAO.findAll();
        if (accountList.isEmpty()) {
            List<Account> list = generateAccounts();
            accountsDAO.saveAll(list);
            for (Account acc : list) {
                accounts.add(acc.getId());
            }
        } else {
            for (Account acc : accountList) {
                accounts.add(acc.getId());
            }
        }
    }

    private List<Account> generateAccounts() {
        Random rand = new Random();
        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            accounts.add(new Account(UUID.randomUUID(), rand.nextDouble(MAX_BALANCE)));
        }
        return accounts;
    }

    @Scheduled(fixedRate = 1000)
    @Override
    public void generateTransfers() {
        Random rand = new Random();
        List<Transfer> events = new ArrayList<>();
        if (accounts.isEmpty()) {
            initAccounts();
        }
        for (int i = 0; i < 5; i ++) {
            Transfer transfer = Transfer.builder()
                .id(UUID.randomUUID())
                .amount(rand.nextDouble(MAX_BALANCE))
                .senderId(accounts.get(rand.nextInt(accounts.size())))
                .receiverId(accounts.get(rand.nextInt(accounts.size())))
                .build();
            events.add(transfer);
        }
        kafkaProducerService.sendEvent(events);
    }

}
