package com.example.consumer.service;

import com.example.consumer.model.Transfer;
import com.example.consumer.model.TransferStatus;
import com.example.consumer.dao.TransfersDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransfersService {

    private final TransfersDAO transfersDAO;

    public Transfer createTransfer(Transfer transfer) {
        log.info("createTransfer(): transfer with id {} is PROCESSING", transfer.getId());
        transfer.setStatus(TransferStatus.PROCESSING);
        return transfersDAO.save(transfer);
    }

    public Transfer completeTransfer(Transfer transfer) {
        log.info("completeTransfer(): transfer with id {} is COMPLETED", transfer.getId());
        transfer.setStatus(TransferStatus.COMPLETED);
        return transfersDAO.save(transfer);
    }

    public Transfer rejectTransfer(Transfer transfer) {
        log.info("rejectTransfer(): transfer with id {} is REJECTED", transfer.getId());
        transfer.setStatus(TransferStatus.REJECTED);
        return transfersDAO.save(transfer);
    }

    public Optional<Transfer> getById(UUID id) {
        return transfersDAO.findById(id);
    }
}
