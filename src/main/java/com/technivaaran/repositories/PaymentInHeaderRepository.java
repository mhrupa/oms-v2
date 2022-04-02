package com.technivaaran.repositories;

import com.technivaaran.entities.PaymentInHeader;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentInHeaderRepository extends JpaRepository<PaymentInHeader, Long> {
    
}
