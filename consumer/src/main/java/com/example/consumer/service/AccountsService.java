package com.example.consumer.service;

import com.example.consumer.model.Account;
import com.example.consumer.model.Transfer;
import com.example.consumer.dao.AccountsDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountsService {

    private Map<UUID, Account> accounts = new HashMap<>();
    private final AccountsDAO accountsDAO;

    public Map<UUID, Account> initAccounts() {
        log.info("Get accounts from DB");
        List<Account> accountList = accountsDAO.findAll();
        for (Account acc : accountList) {
            accounts.put(acc.getId(), acc);
        }
        return accounts;
    }

    @Transactional
    public Map<UUID, Account> transferMoney(Account sender, Account receiver, Transfer transfer) {
        //log.info("transferMoney(): transfer id {}", transfer.getId());
        sender.setBalance(sender.getBalance() - transfer.getAmount());
        receiver.setBalance(receiver.getBalance() + transfer.getAmount());
        accountsDAO.save(sender);
        accountsDAO.save(receiver);
        accounts.put(sender.getId(), sender);
        accounts.put(receiver.getId(), receiver);
        return accounts;
    }
}
