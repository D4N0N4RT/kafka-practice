package com.example.office.dao;

import com.example.office.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountsDAO extends JpaRepository<Account, Long> {
}
