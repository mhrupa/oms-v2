package com.technivaaran.repositories;

import java.util.Optional;

import com.technivaaran.entities.PaymentAccountsEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentAccountsRepository extends JpaRepository<PaymentAccountsEntity, Long>{

    Optional<PaymentAccountsEntity> findByAccountName(String paymentAccountName);
    
}
