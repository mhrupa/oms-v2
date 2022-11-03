package com.technivaaran.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.technivaaran.AppUrlConstants;
import com.technivaaran.dto.OmsResponse;
import com.technivaaran.dto.request.OrderRequestDto;
import com.technivaaran.services.SalesOrderService;

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

    @GetMapping("/salesOrderDataForPrint/{challanNo}")
    public ResponseEntity<OmsResponse> getSalesOrderDataForPrinting(@PathVariable Long challanNo) {
        log.info("Get sales oreder data for printing started");
        return salesOrderService.getSalesOrderDataForPrinting(challanNo);
    }

    @GetMapping("/salesOrders")
    public ResponseEntity<OmsResponse> getPendingSalesOrders() {
        log.info("get pending sales oreder started");
        return salesOrderService.getPendingOrders();
    }

    @PostMapping("/updateSalesOrders")
    public ResponseEntity<OmsResponse> updateSalesOrders(@RequestBody OrderRequestDto orderRequestDto) {
        log.info("update sales oreder started");
        return salesOrderService.updateSalesOrder(orderRequestDto);
    }

    @GetMapping("/salesOrders/all")
    public ResponseEntity<OmsResponse> getAllSalesOrders(@RequestParam String fromDate, @RequestParam String toDate) {
        log.info("get all sales oreder started date range from {} to {}", fromDate, toDate);
        return salesOrderService.getAllOrders(fromDate, toDate);
    }

    @PostMapping("/salesOrders/return/{challanNo}")
    public ResponseEntity<OmsResponse> returnSalesOrder(@PathVariable Long challanNo) {
        log.info("return sales oreder started");
        return salesOrderService.returnSalesOrder(challanNo);
    }

    @GetMapping("/salesOrders/getAccountPaymentData")
    public ResponseEntity<OmsResponse> getAccountPaymentData(@RequestParam Long account, @RequestParam int month, @RequestParam int year) {
        log.info("getAccountPaymentData started");
        return salesOrderService.getAccountPaymentData(account, month+1, year);
    }
}
