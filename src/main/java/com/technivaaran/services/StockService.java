package com.technivaaran.services;

import java.util.Map;
import java.util.Optional;

import com.technivaaran.dto.request.StockRequestDto;
import com.technivaaran.entities.ItemMaster;
import com.technivaaran.entities.StockDetails;
import com.technivaaran.entities.StockHeader;
import com.technivaaran.repositories.StockDetailsRepository;
import com.technivaaran.repositories.StockHeaderRepository;
import com.technivaaran.utils.DateUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StockService {

    @Autowired
    private StockHeaderRepository stockHeaderRepository;

    @Autowired
    private StockDetailsRepository stockDetailsRepository;

    @Autowired
    ItemMasterService itemMasterService;

    public void getMaterialDetailsByMaterialName() {
        stockHeaderRepository.findById(1L);
        stockDetailsRepository.findById(1L);
    }

    public void createStockEntry(StockRequestDto stockRequestDto) {
        Optional<ItemMaster> itemOp = itemMasterService.findByItemName(stockRequestDto.getModel());
        if (itemOp.isPresent()) {
            ItemMaster item = itemOp.get();
            // if (item.getPartNo().equalsIgnoreCase(stockRequestDto.getPartNo())) {
            // StockHeader stockHeader = null;
            // Optional<StockHeader> stockHeaderOp = stockHeaderRepository
            // .findByLocationAndItemMaster(stockRequestDto.getBoxNo(), item);
            // if (!stockHeaderOp.isEmpty()) {
            // stockHeader = stockHeaderOp.get();
            // } else {
            // stockHeader = StockHeader.builder()
            // .itemMaster(item).location(stockRequestDto.getBoxNo())
            // .stockDate(DateUtils.getCurrentDate()).build();
            // }
            // createStockDetail(stockRequestDto, stockHeader);
            // updateStockHeader(stockHeader);
            // }
        }
    }

    private StockDetails createStockDetail(StockRequestDto stockRequestDto, StockHeader stockHeader) {
        StockDetails stockDetails = StockDetails.builder()
                .inQty(stockRequestDto.getQty()).outQty(0).stockHeader(stockHeader)
                .transactionDate(DateUtils.getCurrentDateTime()).user(null).build();

        return stockDetailsRepository.save(stockDetails);
    }

    private void updateStockHeader(StockHeader stockHeader) {
        log.info("update stock header");
        Map<String, String> dataMap = stockDetailsRepository.getByStockHeaderAndTransactionDate(stockHeader.getId(),
                stockHeader.getStockDate());

        log.info("data: " + dataMap);

    }
}
