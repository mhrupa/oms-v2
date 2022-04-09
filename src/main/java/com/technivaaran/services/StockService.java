package com.technivaaran.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.technivaaran.dto.OmsResponse;
import com.technivaaran.dto.request.StockRequestDto;
import com.technivaaran.dto.response.StockResponseDto;
import com.technivaaran.entities.ConfigDetailsEntity;
import com.technivaaran.entities.StockDetails;
import com.technivaaran.entities.StockHeader;
import com.technivaaran.entities.StorageLocationEntity;
import com.technivaaran.entities.User;
import com.technivaaran.entities.VendorEntity;
import com.technivaaran.enums.StockTransactionType;
import com.technivaaran.enums.StockType;
import com.technivaaran.mapper.StockHeaderResponseMapper;
import com.technivaaran.repositories.StockDetailsRepository;
import com.technivaaran.repositories.StockHeaderRepository;

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

    @Autowired
    private UserService userService;

    private static final String STOCK_UPDATE_SUCCESS = "Stock updated successfully.";

    public void getMaterialDetailsByMaterialName() {
        stockHeaderRepository.findById(1L);
        stockDetailsRepository.findById(1L);
    }

    public List<StockResponseDto> getStockHeader() {
        List<StockResponseDto> stockResponseDtos = new ArrayList<>();
        stockHeaderRepository.findByClosingQtyGreaterThan(0).forEach(
                stockHeader -> stockResponseDtos
                        .add(stockHeaderResponseMapper.convertToDto(stockHeader, StockTransactionType.NORMAL)));
        return stockResponseDtos;
    }

    @Transactional
    public ResponseEntity<OmsResponse> createStockEntry(StockRequestDto stockRequestDto) {
        Optional<ConfigDetailsEntity> configOp = configDetailsService.findById(stockRequestDto.getConfigId());
        if (configOp.isEmpty()) {
            return new ResponseEntity<>(OmsResponse.builder().message("Invalid configuration received.").build(),
                    HttpStatus.BAD_REQUEST);
        }
        Optional<StorageLocationEntity> storageLocationOp = storageLocationService
                .findByLocationName(stockRequestDto.getBoxName());
        if (storageLocationOp.isEmpty()) {
            return new ResponseEntity<>(OmsResponse.builder().message("Invalid box no received.").build(),
                    HttpStatus.BAD_REQUEST);
        }
        Optional<VendorEntity> vendorOp = vendorService.findById(stockRequestDto.getVendorId());
        if (vendorOp.isEmpty()) {
            return new ResponseEntity<>(OmsResponse.builder().message("Invalid Vendor name received.").build(),
                    HttpStatus.BAD_REQUEST);
        }
        ConfigDetailsEntity configEntity = configOp.get();
        Optional<StockHeader> stockHeaderOp = findStockHeaderByLocationAndModelAndPartAndConfigAndVendorAndBuyPrice(
                storageLocationOp.get(), configEntity, vendorOp.get(), stockRequestDto.getBuyPrice());
        StockHeader stockHeader = null;
        User user = userService.getUserById(stockRequestDto.getUserId());
        if (stockHeaderOp.isPresent()) {
            stockHeader = stockHeaderOp.get();
            if (stockHeader.getClosingQty() > 0) {
                return new ResponseEntity<>(
                        OmsResponse.builder().message("Stock data already available click on table row to update data.")
                                .build(),
                        HttpStatus.BAD_REQUEST);
            } else {
                stockHeader.setInQty(stockRequestDto.getQty());
                stockHeader.setRemark(stockRequestDto.getRemarkText());
                stockHeader.setClosingQty(stockRequestDto.getQty());
                stockHeader.setStorageLocation(storageLocationOp.get());
                stockHeader.setItemMaster(configEntity.getPartEntity().getItemMaster());
                stockHeader.setPartEntity(configEntity.getPartEntity());
                stockHeader.setConfigDetailsEntity(configEntity);
                stockHeader.setDetails(stockRequestDto.getDetails());
                stockHeader.setBuyPrice(stockRequestDto.getBuyPrice());
                stockHeader.setSellPrice(stockRequestDto.getSellPrice());
                stockHeader.setVendor(vendorOp.get());

                StockDetails stockDetails = createStockDetails(stockRequestDto.getBuyPrice(),
                        stockRequestDto.getSellPrice(), user, stockRequestDto.getQty(), 0);
                stockDetails.setStockHeader(stockHeader);
                stockDetails.setType(StockType.IN.type);

                return updateStock(stockHeader, stockDetails, StockTransactionType.NORMAL);
            }
        }

        stockHeader = StockHeader.builder()
                .openingQty(0)
                .inQty(stockRequestDto.getQty())
                .outQty(0)
                .remark(stockRequestDto.getRemarkText())
                .closingQty(stockRequestDto.getQty())
                .storageLocation(storageLocationOp.get())
                .itemMaster(configEntity.getPartEntity().getItemMaster())
                .partEntity(configEntity.getPartEntity()).configDetailsEntity(configEntity)
                .details(stockRequestDto.getDetails())
                .buyPrice(stockRequestDto.getBuyPrice())
                .sellPrice(stockRequestDto.getSellPrice())
                .vendor(vendorOp.get())
                .build();

        StockDetails stockDetails = createStockDetails(stockRequestDto.getBuyPrice(),
                stockRequestDto.getSellPrice(), user, stockRequestDto.getQty(), 0);
        stockDetails.setStockHeader(stockHeader);
        stockDetails.setType(StockType.IN.type);

        updateStock(stockHeader, stockDetails, StockTransactionType.NORMAL);

        return new ResponseEntity<>(OmsResponse.builder().message(STOCK_UPDATE_SUCCESS)
                .data(stockHeaderResponseMapper.convertToDto(stockHeader, StockTransactionType.NORMAL)).build(),
                HttpStatus.OK);

    }

    public Optional<StockHeader> findStockHeaderByLocationAndModelAndPartAndConfigAndVendorAndBuyPrice(
            StorageLocationEntity storageLocationEntity, ConfigDetailsEntity configDetailsEntity, VendorEntity vendor,
            float buyPrice) {

        return stockHeaderRepository
                .findByStorageLocationAndItemMasterAndPartEntityAndConfigDetailsEntityAndVendorAndBuyPrice(
                        storageLocationEntity, configDetailsEntity.getPartEntity().getItemMaster(),
                        configDetailsEntity.getPartEntity(), configDetailsEntity, vendor, buyPrice);
    }

    public ResponseEntity<OmsResponse> updateStockHeaderAndStockDetaisById(long id, StockRequestDto stockRequestDto) {
        Optional<StockHeader> stockHeaderOp = stockHeaderRepository.findById(id);

        if (stockHeaderOp.isPresent()) {
            StockHeader stockHeader = stockHeaderOp.get();
            Optional<ConfigDetailsEntity> configOp = configDetailsService
                    .findById(stockRequestDto.getUpdatedConfigId());
            if (configOp.isEmpty()) {
                return new ResponseEntity<>(OmsResponse.builder().message("Invalid configuration received.").build(),
                        HttpStatus.BAD_REQUEST);
            }
            Optional<StorageLocationEntity> storageLocationOp = storageLocationService
                    .findByLocationName(stockRequestDto.getUpdatedBoxName());
            if (storageLocationOp.isEmpty()) {
                return new ResponseEntity<>(OmsResponse.builder().message("Invalid box no received.").build(),
                        HttpStatus.BAD_REQUEST);
            }
            Optional<VendorEntity> vendorOp = vendorService.findById(stockRequestDto.getUpdatedVendorId());
            if (vendorOp.isEmpty()) {
                return new ResponseEntity<>(OmsResponse.builder().message("Invalid Vendor name received.").build(),
                        HttpStatus.BAD_REQUEST);
            }
            ConfigDetailsEntity configEntity = configOp.get();
            Optional<StockHeader> updateStockHeaderOp = findStockHeaderByLocationAndModelAndPartAndConfigAndVendorAndBuyPrice(
                    storageLocationOp.get(), configEntity, vendorOp.get(), stockRequestDto.getUpdatedBuyPrice());

            User user = userService.getUserById(stockRequestDto.getUserId());
            if (updateStockHeaderOp.isPresent()) {
                if (updateStockHeaderOp.get().getId().equals(stockHeader.getId())) {
                    if (stockRequestDto.getUpdatedDetails().equalsIgnoreCase(stockHeader.getDetails())) {
                        return updateStockHeaderAndStockDetais(stockHeader, stockRequestDto.getStockType(),
                                stockRequestDto.getQty(), stockRequestDto.getSellPrice(),
                                stockRequestDto.getUpdatedBuyPrice(), user, StockTransactionType.NORMAL);
                    } else {
                        stockHeader.setDetails(stockRequestDto.getUpdatedDetails());
                        return updateStockHeaderAndStockDetais(stockHeader, stockRequestDto.getStockType(),
                                stockRequestDto.getQty(), stockRequestDto.getSellPrice(),
                                stockRequestDto.getUpdatedBuyPrice(), user, StockTransactionType.CONVERT);
                    }

                } else {
                    ResponseEntity<OmsResponse> response = updateStockHeaderAndStockDetais(updateStockHeaderOp.get(),
                            stockRequestDto.getStockType(),
                            stockHeader.getClosingQty() + stockRequestDto.getQty(), stockRequestDto.getSellPrice(),
                            stockRequestDto.getUpdatedBuyPrice(), user, StockTransactionType.CONVERT);

                    stockHeader.setOutQty(stockHeader.getClosingQty());
                    stockHeader.setClosingQty(0);

                    StockDetails stockDetails = createStockDetails(0, 0, user, 0, stockHeader.getClosingQty());
                    stockDetails.setType(StockType.CONVERT.type);
                    stockDetails.setRefStockHeaderId(updateStockHeaderOp.get().getId());

                    updateStock(stockHeader, stockDetails, StockTransactionType.CONVERT);

                    return response;
                }
            } else {
                StockHeader stockHeaderNew = StockHeader.builder()
                        .openingQty(0)
                        .inQty(stockHeader.getClosingQty() + stockRequestDto.getQty())
                        .openingQty(0)
                        .outQty(0)
                        .remark(stockHeader.getRemark())
                        .closingQty(stockHeader.getClosingQty() + stockRequestDto.getQty())
                        .storageLocation(storageLocationOp.get())
                        .itemMaster(configEntity.getPartEntity().getItemMaster())
                        .partEntity(configEntity.getPartEntity()).configDetailsEntity(configEntity)
                        .details(stockRequestDto.getUpdatedDetails())
                        .buyPrice(stockRequestDto.getUpdatedBuyPrice())
                        .sellPrice(stockRequestDto.getSellPrice())
                        .vendor(vendorOp.get())
                        .build();

                StockDetails stockDetails = createStockDetails(stockRequestDto.getUpdatedBuyPrice(),
                        stockRequestDto.getSellPrice(), user,
                        stockHeader.getClosingQty() + stockRequestDto.getQty(), 0);
                stockDetails.setType(StockType.IN.type);

                updateStock(stockHeaderNew, stockDetails, StockTransactionType.CONVERT);

                /** Update old stock row */
                stockHeader.setOutQty(stockHeader.getClosingQty());
                stockHeader.setClosingQty(0);

                /** Create row for old stock */
                StockDetails stockDetailsOrg = createStockDetails(0, 0, user, 0, stockHeader.getClosingQty());
                stockDetailsOrg.setType(StockType.CONVERT.type);
                stockDetailsOrg.setRefStockHeaderId(stockHeaderNew.getId());

                updateStock(stockHeader, stockDetailsOrg, StockTransactionType.CONVERT);

                return new ResponseEntity<>(OmsResponse.builder().message(STOCK_UPDATE_SUCCESS)
                        .data(stockHeaderResponseMapper.convertToDto(stockHeaderNew, StockTransactionType.CONVERT))
                        .build(), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(OmsResponse.builder().message("Invalid stock header received.").build(),
                    HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<OmsResponse> updateStockHeaderAndStockDetais(StockHeader stockHeader,
            String stockType, float quantity, float sellPrice, float buyPrice, User user,
            StockTransactionType stockTransactionType) {
        log.info("update stock header by stockHeader id and stockType");
        StockDetails stockDetails = null;
        switch (StockType.valueOf(stockType.toUpperCase())) {
            case IN: {
                if ((stockHeader.getClosingQty() + quantity) >= 0) {
                    stockHeader.setInQty(stockHeader.getInQty() + quantity);
                    stockHeader.setClosingQty(stockHeader.getClosingQty() + quantity);
                    stockHeader.setSellPrice(sellPrice);

                    stockDetails = createStockDetails(buyPrice, sellPrice, user, quantity, 0);
                    stockDetails.setType(StockType.IN.type);
                } else {
                    return new ResponseEntity<>(
                            OmsResponse.builder().message("Invalid stock quantitys received.").build(),
                            HttpStatus.BAD_REQUEST);
                }
                break;
            }
            case OUT: {
                stockHeader.setOutQty(stockHeader.getOutQty() + quantity);
                stockHeader.setClosingQty(stockHeader.getClosingQty() - quantity);

                stockDetails = createStockDetails(buyPrice, sellPrice, user, 0, quantity);
                stockDetails.setType(StockType.OUT.type);
                break;
            }
            case RETURN: {
                return new ResponseEntity<>(OmsResponse.builder().message("Invalid stock type received.").build(),
                        HttpStatus.BAD_REQUEST);
            }
            default:
                return new ResponseEntity<>(OmsResponse.builder().message("Invalid stock type received.").build(),
                        HttpStatus.BAD_REQUEST);
        }
        return updateStock(stockHeader, stockDetails, stockTransactionType);
    }

    @Transactional
    private ResponseEntity<OmsResponse> updateStock(StockHeader stockHeader, StockDetails stockDetails,
            StockTransactionType stockTransactionType) {
        stockHeader.setStockDate(LocalDate.now());
        stockHeader = stockHeaderRepository.save(stockHeader);

        stockDetails.setStockHeader(stockHeader);
        stockDetailsRepository.save(stockDetails);
        return new ResponseEntity<>(OmsResponse.builder().message(STOCK_UPDATE_SUCCESS)
                .data(stockHeaderResponseMapper.convertToDto(stockHeader, stockTransactionType)).build(),
                HttpStatus.OK);
    }

    private StockDetails createStockDetails(float buyPrice,
            float sellPrice, User user, float inQty, float outQty) {
        return StockDetails.builder()
                .transactionDate(LocalDateTime.now())
                .buyPrice(buyPrice)
                .sellPrice(sellPrice)
                .inQty(inQty)
                .outQty(outQty)
                .user(user)
                .build();

    }

    public Optional<StockHeader> getStockHeaderByStockHeaderId(long stockHeaderId) {
        return stockHeaderRepository.findById(stockHeaderId);
    }

    public Optional<StockDetails> findStockDetailsById(long stockDetailId) {
        return stockDetailsRepository.findById(stockDetailId);
    }

    public Optional<StockDetails> findLatestStockDetailsByStockHeader(long stockHeaderId) {

        return stockDetailsRepository.findLatestStockDetailsByStockHeader(stockHeaderId);
    }
}
