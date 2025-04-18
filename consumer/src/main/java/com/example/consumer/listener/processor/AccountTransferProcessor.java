package com.example.consumer.listener.processor;

import com.example.consumer.model.Account;
import com.example.consumer.model.Transfer;
import com.example.consumer.service.AccountsService;
import com.example.consumer.service.TransfersService;
import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountTransferProcessor {

    private final AccountsService accountsService;
    private final TransfersService transfersService;
    private Map<UUID, Account> accounts;

    @PostConstruct
    void initAccounts() {
        accounts = accountsService.initAccounts();
    }


    @Observed
    public void processTransfer(Transfer transfer) {
        log.info("processTransfer(): transfer id = {}, sender id = {}, receiver id = {}", transfer.getId(), transfer.getSenderId(), transfer.getReceiverId());
        if (transfersService.getById(transfer.getId()).isPresent()) {
            log.error("processTransfer() ERROR: transfer already exists");
            return;
        }
        Account sender = accounts.get(transfer.getSenderId());
        Account receiver = accounts.get(transfer.getReceiverId());
        if (Objects.isNull(sender) || Objects.isNull(receiver)) {
            log.error("processTransfer() ERROR: one of the accounts included in transfer doesn't exist. Aborting transfer");
            return;
        } else if (sender.getBalance() < transfer.getAmount()) {
            log.error("processTransfer() ERROR: sender doesn't have enough money to complete transfer");
            return;
        }
        Transfer createdTransfer = transfersService.createTransfer(transfer);
        Map<UUID, Account> accountMap = accountsService.transferMoney(sender, receiver, createdTransfer);
        if (Objects.nonNull(accountMap)) {
            transfersService.completeTransfer(createdTransfer);
            accounts = accountMap;
        } else {
            transfersService.rejectTransfer(createdTransfer);
        }
    }
}
