package com.technivaaran.controllers;

import com.technivaaran.AppUrlConstants;
import com.technivaaran.dto.OmsResponse;
import com.technivaaran.dto.request.OrderRequestDto;
import com.technivaaran.services.SalesOrderService;

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
public class SalesOrderController {

    @Autowired
    private SalesOrderService salesOrderService;

    @PostMapping("/salesOrders")
    public ResponseEntity<OmsResponse> createSalesOrder(@RequestBody OrderRequestDto orderRequestDto) {
        log.info("create sales oreder started");
        return salesOrderService.createSalesOrder(orderRequestDto);
    }

    @GetMapping("/salesOrders")
    public ResponseEntity<OmsResponse> getPendingSalesOrders() {
        log.info("get pending sales oreder started");
        return salesOrderService.getPendingOrders();
    }
}
