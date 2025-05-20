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

    public Transfer changeTransferStatus(Transfer transfer, TransferStatus status) {
        log.info("createTransfer(): transfer with id {} is {}", transfer.getId(), status);
        transfer.setStatus(status);
        return transfersDAO.save(transfer);
    }

    public Optional<Transfer> getById(UUID id) {
        return transfersDAO.findById(id);
    }
}
