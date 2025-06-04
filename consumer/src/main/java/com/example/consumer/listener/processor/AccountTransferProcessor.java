package com.example.consumer.listener.processor;

import com.example.consumer.model.Account;
import com.example.consumer.model.Transfer;
import com.example.consumer.model.TransferStatus;
import com.example.consumer.service.AccountsService;
import com.example.consumer.service.TransfersService;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountTransferProcessor {

    private final AccountsService accountsService;
    private final TransfersService transfersService;


    @Observed
    public void processTransfer(Transfer transfer) {
        log.info("processTransfer(): transfer id = {}, sender id = {}, receiver id = {}", transfer.getId(), transfer.getSenderId(), transfer.getReceiverId());
        if (transfersService.getById(transfer.getId()).isPresent()) {
            log.error("processTransfer() ERROR: transfer already exists");
            return;
        }
        accountsService.accountsCheck(transfer);
        Transfer createdTransfer = transfersService.changeTransferStatus(transfer, TransferStatus.PROCESSING);
        if (accountsService.transferMoney(createdTransfer)) {
            transfersService.changeTransferStatus(createdTransfer, TransferStatus.COMPLETED);
        } else {
            transfersService.changeTransferStatus(createdTransfer, TransferStatus.REJECTED);
        }
    }
}
