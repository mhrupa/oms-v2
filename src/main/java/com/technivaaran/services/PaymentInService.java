package com.technivaaran.services;

import com.technivaaran.dto.OmsResponse;
import com.technivaaran.dto.request.PaymentInRequestDto;
import com.technivaaran.repositories.PaymentInDetailsRepository;
import com.technivaaran.repositories.PaymentInHeaderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PaymentInService {

    @Autowired
    PaymentInHeaderRepository paymentInHeaderRepository;

    @Autowired
    PaymentInDetailsRepository paymentInDetailsRepository;

    public ResponseEntity<OmsResponse> savePaymentIn(PaymentInRequestDto paymentInRequestDto) {
        
        return new ResponseEntity<>(OmsResponse.builder().message("Payment record created successfully.")
                        // .data(part)
                        .build(), HttpStatus.CREATED);
    }
    
}
