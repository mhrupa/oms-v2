package com.technivaaran.controllers;

import com.technivaaran.AppUrlConstants;
import com.technivaaran.dto.OmsResponse;
import com.technivaaran.dto.request.StockRequestDto;
import com.technivaaran.services.StockService;
import com.technivaaran.utils.JsonUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(AppUrlConstants.BASE_URL)
@Slf4j
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping("/stock")
    public void getStockData() {
        log.info("get stock data called");

        stockService.getMaterialDetailsByMaterialName();
    }

    @PostMapping("/stock")
    public ResponseEntity<OmsResponse> saveStock(@RequestBody StockRequestDto stockRequestDto){
        log.info("started save stock", JsonUtils.toJson(stockRequestDto));
        stockService.createStockEntry(stockRequestDto);        
        return new ResponseEntity<>(OmsResponse.builder().message("Stock data updated successfully").build(),
				HttpStatus.OK);
    }


}
