package com.technivaaran.services;

import java.util.List;
import java.util.Optional;

import com.technivaaran.dto.OmsResponse;
import com.technivaaran.entities.PaymentAccountsEntity;
import com.technivaaran.repositories.PaymentAccountsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PaymentAccountsService {

    @Autowired
    private PaymentAccountsRepository paymentAccountsRepository;

    public ResponseEntity<OmsResponse> createPaymentAccount(String paymentAccountName) {
        Optional<PaymentAccountsEntity> paymentAccountOp = paymentAccountsRepository
                .findByAccountName(paymentAccountName);
        if (paymentAccountOp.isEmpty()) {
            PaymentAccountsEntity accountsEntity = PaymentAccountsEntity.builder()
                    .accountName(paymentAccountName)
                    .build();
            paymentAccountsRepository.save(accountsEntity);
            return new ResponseEntity<>(OmsResponse.builder().message("Payment Account created successfully.")
                    .data(accountsEntity).build(), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(OmsResponse.builder().message("Payment Account already exists.")
                    .build(), HttpStatus.BAD_REQUEST);
        }
    }

    public List<PaymentAccountsEntity> getAllPaymentAccounts() {
        return paymentAccountsRepository.findAll();
    }

    public Optional<PaymentAccountsEntity> findById(Long id) {
        return paymentAccountsRepository.findById(id);
    }
}
