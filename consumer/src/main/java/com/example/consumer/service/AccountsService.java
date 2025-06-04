package com.example.consumer.service;

import com.example.consumer.model.Account;
import com.example.consumer.model.Transfer;
import com.example.consumer.dao.AccountsDAO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountsService {

    private final Map<UUID, Account> accounts = new HashMap<>();
    private final AccountsDAO accountsDAO;

    @PostConstruct
    public Map<UUID, Account> initAccounts() {
        log.info("Get accounts from DB");
        List<Account> accountList = accountsDAO.findAll();
        for (Account acc : accountList) {
            accounts.put(acc.getId(), acc);
        }
        return accounts;
    }

    public void accountsCheck(Transfer transfer) {
        Account sender = accounts.get(transfer.getSenderId());
        Account receiver = accounts.get(transfer.getReceiverId());
        if (Objects.isNull(sender) || Objects.isNull(receiver)) {
            log.error("processTransfer() ERROR: one of the accounts included in transfer doesn't exist. Aborting transfer");
        } else if (sender.getBalance() < transfer.getAmount()) {
            log.error("processTransfer() ERROR: sender doesn't have enough money to complete transfer");
        }
    }

    @Transactional
    public boolean transferMoney(Transfer transfer) {
        Account sender = accounts.get(transfer.getSenderId());
        Account receiver = accounts.get(transfer.getReceiverId());
        sender.setBalance(sender.getBalance() - transfer.getAmount());
        receiver.setBalance(receiver.getBalance() + transfer.getAmount());
        accountsDAO.save(sender);
        accountsDAO.save(receiver);
        accounts.put(sender.getId(), sender);
        accounts.put(receiver.getId(), receiver);
        return true;
    }
}
