package com.example.office.service;


import com.example.office.dao.AccountsDAO;
import com.example.office.model.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountsDAO accountsDAO;
    private final List<UUID> accounts;
    public static final Double MAX_BALANCE = 3500000.0;

    List<UUID> getAccounts() {
        return accounts;
    }

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
}
