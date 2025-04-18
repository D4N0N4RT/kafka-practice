package com.example.consumer.dao;

import com.example.consumer.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountsDAO extends JpaRepository<Account, UUID> {
}
