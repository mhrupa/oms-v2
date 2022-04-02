package com.technivaaran.controllers;

import com.technivaaran.AppUrlConstants;
import com.technivaaran.dto.OmsResponse;
import com.technivaaran.dto.request.PaymentInRequestDto;
import com.technivaaran.services.PaymentInService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(AppUrlConstants.BASE_URL)
public class PaymentInController {
    
    @Autowired
    private PaymentInService paymentInService;


    @PostMapping("/paymentIn")
    public ResponseEntity<OmsResponse> savePaymentIn(@RequestBody PaymentInRequestDto paymentInRequestDto) {
        log.info("Started saving payment In.");
        return paymentInService.savePaymentIn(paymentInRequestDto);
    }
}
