package com.technivaaran.controllers;

import com.technivaaran.AppUrlConstants;
import com.technivaaran.dto.OmsResponse;
import com.technivaaran.dto.request.StockRequestDto;
import com.technivaaran.dto.response.StockResponseDto;
import com.technivaaran.services.StockService;
import com.technivaaran.utils.JsonUtils;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AppUrlConstants.BASE_URL)
@Slf4j
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping("/stock")
    public List<StockResponseDto> getStockData() {
        log.info("get all stock data called");
        return stockService.getStockHeader();
    }

    @PostMapping("/stock")
    public ResponseEntity<OmsResponse> saveStock(@RequestBody StockRequestDto stockRequestDto) {
        log.info("started save stock", JsonUtils.toJson(stockRequestDto));
        return stockService.createStockEntry(stockRequestDto);
    }

    @PostMapping("/stock/delRow/{stockHeaderId}")
    public ResponseEntity<OmsResponse> deleteRowForZeroStock(@PathVariable(name = "stockHeaderId") long stockHeaderId) {
        log.info("started delete row for stoch header ID {}", stockHeaderId);
        return stockService.deleteRowForZerorStock(stockHeaderId);
    }

    @PostMapping("/stock/{id}")
    public ResponseEntity<OmsResponse> updateStockById(@RequestBody StockRequestDto stockRequestDto,
            @PathVariable(name = "id") long id) {
        log.info("started update stock {}, id {}", stockRequestDto.getQty(), stockRequestDto.getStockHeaderId());
        return stockService.updateStockHeaderAndStockDetaisById(id, stockRequestDto);
    }
}
