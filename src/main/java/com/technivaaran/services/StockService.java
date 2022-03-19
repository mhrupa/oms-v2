package com.technivaaran.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.technivaaran.dto.OmsResponse;
import com.technivaaran.dto.request.StockRequestDto;
import com.technivaaran.dto.response.StockResponseDto;
import com.technivaaran.entities.ConfigDetailsEntity;
import com.technivaaran.entities.ItemMaster;
import com.technivaaran.entities.PartEntity;
import com.technivaaran.entities.StockDetails;
import com.technivaaran.entities.StockHeader;
import com.technivaaran.entities.StorageLocationEntity;
import com.technivaaran.entities.VendorEntity;
import com.technivaaran.enums.StockType;
import com.technivaaran.mapper.StockHeaderResponseMapper;
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
    private ConfigDetailsService configDetailsService;

    @Autowired
    private StorageLocationService storageLocationService;

    @Autowired
    private StockHeaderResponseMapper stockHeaderResponseMapper;

    @Autowired
    private VendorService vendorService;

    public void getMaterialDetailsByMaterialName() {
        stockHeaderRepository.findById(1L);
        stockDetailsRepository.findById(1L);
    }

    public List<StockResponseDto> getStockHeader() {
        List<StockResponseDto> stockResponseDtos = new ArrayList<>();
        stockHeaderRepository.findAll().forEach(
                stockHeader -> stockResponseDtos.add(stockHeaderResponseMapper.convertToDto(stockHeader)));
        return stockResponseDtos;
    }

    public ResponseEntity<OmsResponse> createStockEntry(StockRequestDto stockRequestDto) {
        Optional<ConfigDetailsEntity> configOp = configDetailsService.findById(stockRequestDto.getConfigId());
        Optional<StorageLocationEntity> storageLocationOp = storageLocationService.findById(stockRequestDto.getBoxId());
        Optional<VendorEntity> vendorOp = vendorService.findById(stockRequestDto.getVendorId());

        if (configOp.isEmpty()) {
            return new ResponseEntity<>(OmsResponse.builder().message("Invalid configuration received.").build(),
                    HttpStatus.BAD_REQUEST);
        }
        if (storageLocationOp.isEmpty()) {
            return new ResponseEntity<>(OmsResponse.builder().message("Invalid box no received.").build(),
                    HttpStatus.BAD_REQUEST);
        }
        if (vendorOp.isEmpty()) {
            return new ResponseEntity<>(OmsResponse.builder().message("Invalid Vendor name received.").build(),
                    HttpStatus.BAD_REQUEST);
        }
        ConfigDetailsEntity configEntity = configOp.get();
        Optional<StockHeader> stockHeaderOp = findStockHeaderByLocationAndModelAndPartAndConfigAndVendorAndBuyPrice(
                storageLocationOp.get(), configEntity.getPartEntity().getItemMaster(), configEntity.getPartEntity(),
                configEntity, vendorOp.get(), stockRequestDto.getBuyPrice());

        StockHeader stockHeader = null;
        if (stockHeaderOp.isPresent()) {
           /**
            stockHeader = stockHeaderOp.get();
            stockHeader.setDetails(stockRequestDto.getDetails());
            stockHeader.setInQty(stockHeader.getInQty() + stockRequestDto.getQty());
            stockHeader.setClosingQty(stockHeader.getInQty() + stockRequestDto.getQty());

            stockHeaderRepository.save(stockHeader);
            */
            return new ResponseEntity<>(
                    OmsResponse.builder().message("Stock data already available click on table row to update data.")
                            .build(), HttpStatus.BAD_REQUEST);
        } else {
            stockHeader = StockHeader.builder()
                    .openingQty(stockRequestDto.getQty())
                    .inQty(stockRequestDto.getQty())
                    .outQty(0).closingQty(stockRequestDto.getQty())
                    .storageLocation(storageLocationOp.get())
                    .itemMaster(configEntity.getPartEntity().getItemMaster())
                    .partEntity(configEntity.getPartEntity()).configDetailsEntity(configEntity)
                    .details(stockRequestDto.getDetails())
                    .buyPrice(stockRequestDto.getBuyPrice())
                    .sellPrice(stockRequestDto.getSellPrice())
                    .vendor(vendorOp.get())
                    .build();
            stockHeaderRepository.save(stockHeader);
            return new ResponseEntity<>(OmsResponse.builder().message("Stock updated successfully.")
                    .data(stockHeaderResponseMapper.convertToDto(stockHeader)).build(),
                    HttpStatus.OK);
        }

    }

    public Optional<StockHeader> findStockHeaderByLocationAndModelAndPartAndConfigAndVendorAndBuyPrice(
            StorageLocationEntity storageLocationEntity, ItemMaster itemMaster, PartEntity partEntity,
            ConfigDetailsEntity configDetailsEntity, VendorEntity vendor, float buyPrice) {

        return stockHeaderRepository
                .findByStorageLocationAndItemMasterAndPartEntityAndConfigDetailsEntityAndVendorAndBuyPrice(
                        storageLocationEntity, itemMaster, partEntity, configDetailsEntity, vendor, buyPrice);
    }

    private StockDetails createStockDetail(StockRequestDto stockRequestDto, StockHeader stockHeader) {
        StockDetails stockDetails = StockDetails.builder()
                .inQty(stockRequestDto.getQty()).outQty(0).stockHeader(stockHeader)
                .transactionDate(DateUtils.getCurrentDateTime()).user(null).build();
        return stockDetailsRepository.save(stockDetails);
    }

    public ResponseEntity<OmsResponse> updateStockHeaderByIdAndType(long stockHeaderId,
            StockRequestDto stockRequestDto) {
        log.info("update stock header by stockHeader id and stockType");
        Optional<StockHeader> stockHeaderOp = stockHeaderRepository.findById(stockHeaderId);
        if (stockHeaderOp.isPresent()) {
            StockHeader stockHeader = stockHeaderOp.get();
            switch (StockType.valueOf(stockRequestDto.getStockType())) {
                case IN: {
                    return updateInStock(stockHeader, stockRequestDto);
                }
                case OUT: {
                    break;
                }
                case RETURN: {
                    break;
                }
                default:
            }
            return null;
        } else {
            return new ResponseEntity<>(OmsResponse.builder().message("Invalid stock id received.").build(),
                    HttpStatus.BAD_REQUEST);
        }

    }

    private ResponseEntity<OmsResponse> updateInStock(StockHeader stockHeader, StockRequestDto stockRequestDto) {
        stockHeader.setInQty(stockHeader.getInQty() + stockRequestDto.getQty());
        stockHeader.setClosingQty(stockHeader.getClosingQty() + stockRequestDto.getQty());
        stockHeader = stockHeaderRepository.save(stockHeader);
        return new ResponseEntity<>(OmsResponse.builder().message("Stock updated successfully.")
                .data(stockHeaderResponseMapper.convertToDto(stockHeader)).build(), HttpStatus.OK);
    }
}
