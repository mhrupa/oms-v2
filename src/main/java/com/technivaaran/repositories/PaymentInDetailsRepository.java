package com.technivaaran.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.technivaaran.entities.PaymentInDetails;

@Repository
public interface PaymentInDetailsRepository extends JpaRepository<PaymentInDetails, Long> {
    public Optional<PaymentInDetails> findByChallanNo(Long challanNo);

    @Modifying
    @Query(value = "DELETE FROM payment_in_details WHERE payment_in_header_id "
            + "IN (SELECT id FROM payment_in_header WHERE payment_in_date <= :tillDate)", nativeQuery = true)
    public void deleteLessThanTransactionDate(String tillDate);
}
