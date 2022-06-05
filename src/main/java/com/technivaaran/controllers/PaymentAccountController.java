package com.technivaaran.controllers;

import java.util.List;
import java.util.Map;

import com.technivaaran.AppUrlConstants;
import com.technivaaran.dto.OmsResponse;
import com.technivaaran.entities.PaymentAccountsEntity;
import com.technivaaran.services.PaymentAccountsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(AppUrlConstants.BASE_URL)
public class PaymentAccountController {

    @Autowired
    PaymentAccountsService paymentAccountsService;

    @PostMapping("/paymentAccounts")
    public ResponseEntity<OmsResponse> savePart(@RequestBody Map<String, String> requestDataMap) {
        log.info("creating part started");

        return paymentAccountsService.createPaymentAccount(requestDataMap.get("paymentAccountName"));
    }

    @GetMapping("/paymentAccounts")
    public List<PaymentAccountsEntity> getAllPaymentAccounts() {
        return paymentAccountsService.getAllPaymentAccounts();
    }

}
