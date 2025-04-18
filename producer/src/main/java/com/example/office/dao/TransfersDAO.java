package com.example.office.dao;

import com.example.office.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransfersDAO extends JpaRepository<Transfer, Long> {
}
