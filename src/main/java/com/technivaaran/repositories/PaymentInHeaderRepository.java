package com.technivaaran.repositories;

import com.technivaaran.entities.PaymentInHeader;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentInHeaderRepository extends JpaRepository<PaymentInHeader, Long> {
    
    @Modifying
    @Query(value = "DELETE FROM payment_in_header WHERE payment_in_date <= :tillDate", nativeQuery = true)
    public void deleteLessThanEqualToPaymentInDate(String tillDate);
}
