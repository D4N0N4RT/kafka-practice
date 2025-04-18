package com.example.office.service;


import com.fasterxml.jackson.core.JsonProcessingException;


public interface AccountTransferEventService {

    void generateTransfers() throws JsonProcessingException;
}
