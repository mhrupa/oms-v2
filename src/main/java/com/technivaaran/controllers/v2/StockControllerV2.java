package com.technivaaran.controllers.v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
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
import com.technivaaran.dto.projections.InventoryRow;
import com.technivaaran.dto.request.StockRequestDto;
import com.technivaaran.services.StockService;
import com.technivaaran.utils.JsonUtils;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(AppUrlConstants.BASE_URL_V2)
@Slf4j
public class StockControllerV2 {

    @Autowired
    private StockService stockService;

    @GetMapping("/stock")
    public Page<InventoryRow> getStockData(@RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "20", required = false) int size) {

        log.info("Retrieving stock page {} with {} rows", page, size);
        var res = stockService.getInventoryData(page, size);
        log.info("Retrieved stock data: {}", res);
        return res;
    }

    @GetMapping("/stock/search")
    public Page<InventoryRow> searchStockData(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "20", required = false) int size,
            @RequestParam(name = "query", required = false) String query) {

        log.info("Retrieving stock page {} with {} rows and query {}", page, size, query);
        var res = stockService.searchInventoryData(page, size, query);
        log.info("Retrieved stock data: {}", res);
        return res;
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
