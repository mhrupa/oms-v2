package com.technivaaran.repositories;

import com.technivaaran.entities.PaymentInDetails;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentInDetailsRepository extends JpaRepository<PaymentInDetails, Long>{
    public Optional<PaymentInDetails> findByChallanNo(Long challanNo);
}
