package com.technivaaran.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.technivaaran.dto.OmsResponse;
import com.technivaaran.dto.request.StockRequestDto;
import com.technivaaran.entities.ConfigDetailsEntity;
import com.technivaaran.entities.ItemMaster;
import com.technivaaran.entities.PartEntity;
import com.technivaaran.entities.StockDetails;
import com.technivaaran.entities.StockHeader;
import com.technivaaran.entities.StorageLocationEntity;
import com.technivaaran.repositories.StockDetailsRepository;
import com.technivaaran.repositories.StockHeaderRepository;
import com.technivaaran.utils.DateUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    ConfigDetailsService configDetailsService;

    @Autowired
    StorageLocationService storageLocationService;

    public void getMaterialDetailsByMaterialName() {
        stockHeaderRepository.findById(1L);
        stockDetailsRepository.findById(1L);
    }

    public List<StockHeader> getStockHeader() {

        
    }

    public ResponseEntity<OmsResponse> createStockEntry(StockRequestDto stockRequestDto) {
        Optional<ConfigDetailsEntity> configOp = configDetailsService.findById(stockRequestDto.getConfigId());

        Optional<StorageLocationEntity> storageLocationOp = storageLocationService.findById(stockRequestDto.getBoxId());

        if (configOp.isEmpty()) {
            return new ResponseEntity<>(OmsResponse.builder().message("Invalid configuration received.").build(),
                    HttpStatus.BAD_REQUEST);
        }
        if (storageLocationOp.isEmpty()) {
            return new ResponseEntity<>(OmsResponse.builder().message("Invalid box no received.").build(),
                    HttpStatus.BAD_REQUEST);
        }
        ConfigDetailsEntity configEntity = configOp.get();

        Optional<StockHeader> stockHeaderOp = findStockHeaderByLocationAndModelAndPartAndConfig(storageLocationOp.get(),
                configEntity.getPartEntity().getItemMaster(), configEntity.getPartEntity(), configEntity);

        StockHeader stockHeader = null;
        if (stockHeaderOp.isPresent()) {
            stockHeader = stockHeaderOp.get();
            stockHeader.setInQty(stockHeader.getInQty() + stockRequestDto.getQty());

            stockHeaderRepository.save(stockHeader);
        } else {
            stockHeader = StockHeader.builder()
                    .openingQty(stockRequestDto.getQty())
                    .inQty(stockRequestDto.getQty())
                    .outQty(0).closingQty(stockRequestDto.getQty())
                    .storageLocation(storageLocationOp.get())
                    .itemMaster(configEntity.getPartEntity().getItemMaster())
                    .partEntity(configEntity.getPartEntity()).configDetailsEntity(configEntity)
                    .build();

            stockHeaderRepository.save(stockHeader);
        }

        return new ResponseEntity<>(OmsResponse.builder().message("Stock updated successfully.")
                .data(stockHeader).build(), HttpStatus.OK);

    }

    public Optional<StockHeader> findStockHeaderByLocationAndModelAndPartAndConfig(
            StorageLocationEntity storageLocationEntity,
            ItemMaster itemMaster, PartEntity partEntity, ConfigDetailsEntity configDetailsEntity) {

        return stockHeaderRepository.findByStorageLocationAndItemMasterAndPartEntityAndConfigDetailsEntity(
                storageLocationEntity, itemMaster, partEntity, configDetailsEntity);
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
