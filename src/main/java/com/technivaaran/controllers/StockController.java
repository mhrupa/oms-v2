package com.technivaaran.controllers;

import com.technivaaran.services.StockService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping("/getStockData")
    public void getStockData() {
        log.info("get stock data called");

        stockService.getMaterialDetailsByMaterialName();
    }


}
