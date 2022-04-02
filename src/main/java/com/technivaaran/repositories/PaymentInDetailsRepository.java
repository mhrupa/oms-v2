package com.technivaaran.repositories;

import com.technivaaran.entities.PaymentInDetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentInDetailsRepository extends JpaRepository<PaymentInDetails, Long>{
    
}
