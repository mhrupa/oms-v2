package com.technivaaran.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.technivaaran.dto.OmsResponse;
import com.technivaaran.dto.request.StockRequestDto;
import com.technivaaran.dto.response.StockResponseDto;
import com.technivaaran.entities.ConfigDetailsEntity;
import com.technivaaran.entities.RemarkEntity;
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

    @Autowired
    private RemarkService remarkService;

    private static final String STOCK_UPDATE_SUCCESS = "Stock updated successfully.";

    public void getMaterialDetailsByMaterialName() {
        stockHeaderRepository.findById(1L);
        stockDetailsRepository.findById(1L);
    }

    public List<StockResponseDto> getStockHeader() {
        List<StockResponseDto> stockResponseDtos = new ArrayList<>();
        stockHeaderRepository.getInventoryData().forEach(object -> stockResponseDtos
                .add(stockHeaderResponseMapper.convertToDto(object, StockTransactionType.NORMAL)));

        //stockHeaderRepository.findByClosingQtyGreaterThan(0).forEach(
//                stockHeader -> stockResponseDtos
//                        .add(stockHeaderResponseMapper.convertToDto(stockHeader, StockTransactionType.NORMAL)));
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
//        Optional<StockHeader> stockHeaderOp = findStockHeaderByLocationAndModelAndPartAndConfigAndVendorAndBuyPrice(
//        Optional<StockHeader> stockHeaderOp = findStockHeaderByLocationAndModelAndPartAndConfigAndVendorAndDetails(
        Optional<StockHeader> stockHeaderOp = findByStorageLocationAndItemMasterAndPartEntityAndConfigDetailsEntityAndVendorAndRemark(
                storageLocationOp.get(), configEntity, vendorOp.get(), stockRequestDto.getDetails(),
                stockRequestDto.getRemarkText());
        StockHeader stockHeader = null;
        User user = userService.getUserById(stockRequestDto.getUserId());
        if (stockHeaderOp.isPresent()) {
            stockHeader = stockHeaderOp.get();
            if (stockHeader.getClosingQty() > 0) {
                return new ResponseEntity<>(
//                        OmsResponse.builder().message("Stock data already available click on table row to update data.")
                        OmsResponse.builder().message("Stock data already available click on inventory table row to update data.")
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
                stockHeader.setSellPrice(stockRequestDto.getSellPrice());
                stockHeader.setVendor(vendorOp.get());
                
                double avgPrice = calculateAveragePrice(configEntity.getId(),
                        configEntity.getPartEntity().getItemMaster().getId(), configEntity.getPartEntity().getId(),
                        stockRequestDto.getBuyPrice(), stockRequestDto.getQty(), stockRequestDto.getDetails(),
                        stockRequestDto.getUpdatedRemark());
                
                stockHeader.setBuyPrice((float)avgPrice);                
                
                stockHeader.setInitialBuyPrice(stockRequestDto.getBuyPrice());

                StockDetails stockDetails = createStockDetails(stockRequestDto.getBuyPrice(),
                        stockRequestDto.getSellPrice(), user, stockRequestDto.getQty(), 0);
                stockDetails.setStockHeader(stockHeader);
                stockDetails.setInitialBuyPrice(stockRequestDto.getBuyPrice());
                stockDetails.setType(StockType.IN.type);

                var response = updateStock(stockHeader, stockDetails, StockTransactionType.NORMAL);
                
                updateAverageBuyAndSellPrice((float) avgPrice, stockRequestDto.getSellPrice(), configEntity.getId(),
                        configEntity.getPartEntity().getItemMaster().getId(), configEntity.getPartEntity().getId(),
                        stockRequestDto.getDetails(), stockRequestDto.getUpdatedRemark());

                return response;
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
                .initialBuyPrice(stockRequestDto.getBuyPrice())
                .sellPrice(stockRequestDto.getSellPrice())
                .vendor(vendorOp.get())
                .build();
        
        double avgPrice = calculateAveragePrice(configEntity.getId(),
                configEntity.getPartEntity().getItemMaster().getId(), configEntity.getPartEntity().getId(),
                stockRequestDto.getBuyPrice(), stockRequestDto.getQty(), stockRequestDto.getDetails(),
                stockRequestDto.getUpdatedRemark());

        stockHeader.setBuyPrice((float)avgPrice);
        
        StockDetails stockDetails = createStockDetails(stockRequestDto.getBuyPrice(),
                stockRequestDto.getSellPrice(), user, stockRequestDto.getQty(), 0);
        stockDetails.setStockHeader(stockHeader);
        stockDetails.setInitialBuyPrice(stockRequestDto.getBuyPrice());
        stockDetails.setType(StockType.IN.type);

        updateStock(stockHeader, stockDetails, StockTransactionType.NORMAL);

        updateAverageBuyAndSellPrice((float) avgPrice, stockRequestDto.getSellPrice(), configEntity.getId(),
                configEntity.getPartEntity().getItemMaster().getId(), configEntity.getPartEntity().getId(),
                stockRequestDto.getDetails(), stockRequestDto.getUpdatedRemark());
        
        return new ResponseEntity<>(OmsResponse.builder().message(STOCK_UPDATE_SUCCESS)
                .data(stockHeaderResponseMapper.convertToDto(stockHeader, StockTransactionType.NORMAL)).build(),
                HttpStatus.OK);

    }
    
    /**
     * 
     * @param configDetailsId
     * @param itemMasterId
     * @param partId
     * @param buyPrice
     * @param qty
     * @return
     */
    public double calculateAveragePrice(Long configDetailsId, Long itemMasterId, Long partId, float buyPrice,
            float qty, String details, String remark) {

        var sumAndRowCountForBuyPrice = getSumAndRowCountForBuyPrice(configDetailsId, itemMasterId, partId, details,
                remark);

        double sumBuyPrice = ObjectUtils.isEmpty(sumAndRowCountForBuyPrice.get("priceSum")) ? 0
                : (double) sumAndRowCountForBuyPrice.get("priceSum");
        double itemQty = ObjectUtils.isEmpty(sumAndRowCountForBuyPrice.get("rowCount")) ? 0
                : (double) sumAndRowCountForBuyPrice.get("rowCount");

        if ((itemQty + qty) != 0) {
            return Math.ceil((sumBuyPrice + (buyPrice * qty)) / (itemQty + qty));
        } else {
            return 0;
        }
        
    }
    
    public double calculateAveragePriceForDetailsChange(Long configDetailsId, Long itemMasterId, Long partId,
            String details, String remark) {

        var avgPrice = getAveragePriceForConfig(configDetailsId, itemMasterId, partId, details, remark);

        return Math.ceil(avgPrice);
    }

    public Optional<StockHeader> findStockHeaderByLocationAndModelAndPartAndConfigAndVendorAndDetails1(
            StorageLocationEntity storageLocationEntity, ConfigDetailsEntity configDetailsEntity, VendorEntity vendor,
            String details) {

        return stockHeaderRepository
                .findByStorageLocationAndItemMasterAndPartEntityAndConfigDetailsEntityAndVendorAndDetailsAndRowDelStatus(
                        storageLocationEntity, configDetailsEntity.getPartEntity().getItemMaster(),
                        configDetailsEntity.getPartEntity(), configDetailsEntity, vendor, details, false);
    }
    
    public Optional<StockHeader> findByStorageLocationAndItemMasterAndPartEntityAndConfigDetailsEntityAndVendorAndRemark(
            StorageLocationEntity storageLocationEntity, ConfigDetailsEntity configDetailsEntity, VendorEntity vendor,
            String details, String remark) {

        return stockHeaderRepository
                .findByStorageLocationAndItemMasterAndPartEntityAndConfigDetailsEntityAndVendorAndDetailsAndRowDelStatusAndRemark(
                        storageLocationEntity, configDetailsEntity.getPartEntity().getItemMaster(),
                        configDetailsEntity.getPartEntity(), configDetailsEntity, vendor, details, false, remark);
    }
    
    public Optional<StockHeader> findStockHeaderByLocationAndModelAndPartAndConfigAndVendorAndBuyPrice(
            StorageLocationEntity storageLocationEntity, ConfigDetailsEntity configDetailsEntity, VendorEntity vendor,
            float buyPrice) {

        return stockHeaderRepository
                .findByStorageLocationAndItemMasterAndPartEntityAndConfigDetailsEntityAndVendorAndBuyPrice(
                        storageLocationEntity, configDetailsEntity.getPartEntity().getItemMaster(),
                        configDetailsEntity.getPartEntity(), configDetailsEntity, vendor, buyPrice);
    }
    
    public Map<String, Object> getSumAndRowCountForBuyPrice(Long configDetailsId, Long itemMasterId, Long partId,
            String details, String remark) {

        return stockHeaderRepository.getSumAndRowCountForBuyPrice(configDetailsId, itemMasterId, partId, details,
                remark);
    }
    
    public double getAveragePriceForConfig(Long configDetailsId, Long itemMasterId, Long partId, String details,
            String remark) {

        return stockHeaderRepository.getAveragePriceForConfig(configDetailsId, itemMasterId, partId, details, remark);
    }

    public void updateAverageBuyAndSellPrice(float buyPrice, float sellPrice, Long configDetailsId, Long itemMasterId,
            Long partId, String details, String remark) {
        try {
            stockHeaderRepository.updateAverageBuyAndSellPrice(buyPrice, sellPrice, configDetailsId, itemMasterId,
                    partId, details, remark);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Transactional
    public ResponseEntity<OmsResponse> updateStockHeaderAndStockDetaisById(long id, StockRequestDto stockRequestDto) {
        Optional<StockHeader> stockHeaderOp = stockHeaderRepository.findById(id);

        if (stockHeaderOp.isEmpty()) {
            return new ResponseEntity<>(OmsResponse.builder().message("Invalid stock header received.").build(),
                    HttpStatus.BAD_REQUEST);
        }

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
        // Optional<StockHeader> updateStockHeaderOp =
        // findStockHeaderByLocationAndModelAndPartAndConfigAndVendorAndDetails(
        Optional<StockHeader> updateStockHeaderOp = findByStorageLocationAndItemMasterAndPartEntityAndConfigDetailsEntityAndVendorAndRemark(
                storageLocationOp.get(), configEntity, vendorOp.get(), stockRequestDto.getUpdatedDetails(),
                stockRequestDto.getUpdatedRemark());

        User user = userService.getUserById(stockRequestDto.getUserId());

        ResponseEntity<OmsResponse> responseEntity = null;
        if (updateStockHeaderOp.isPresent()) {
            double avgPrice = 0;
            if (updateStockHeaderOp.get().getId().equals(stockHeader.getId())) { // if we are updating same stockHeader
//                    if (stockRequestDto.getUpdatedDetails().equalsIgnoreCase(stockHeader.getDetails())) {
//                        // in this scenario we are simple updating the quantity
//                        avgPrice = calculateAveragePrice(configEntity.getId(),
//                                configEntity.getPartEntity().getItemMaster().getId(),
//                                configEntity.getPartEntity().getId(),
//                                stockRequestDto.getUpdatedBuyPrice(), stockRequestDto.getQty(),
//                                stockRequestDto.getUpdatedDetails(), stockRequestDto.getUpdatedRemark());
//                        responseEntity = updateStockHeaderAndStockDetais(stockHeader, stockRequestDto.getStockType(),
//                                stockRequestDto.getQty(), stockRequestDto.getSellPrice(),
//                                stockRequestDto.getUpdatedBuyPrice(), user, StockTransactionType.NORMAL,
//                                stockRequestDto.getUpdatedRemark());
//                    } else {
//                        // in this scenario we are updating the existing entry after check if the details are mismatching
//                        avgPrice = calculateAveragePriceForDetailsChange(configEntity.getId(),
//                                configEntity.getPartEntity().getItemMaster().getId(),
//                                configEntity.getPartEntity().getId(), stockHeader.getDetails(),
//                                stockHeader.getRemark());
//                        stockHeader.setDetails(stockRequestDto.getUpdatedDetails());
//                        responseEntity = updateStockHeaderAndStockDetais(stockHeader, stockRequestDto.getStockType(),
//                                stockRequestDto.getQty(), stockRequestDto.getSellPrice(),
//                                stockRequestDto.getUpdatedBuyPrice(), user, StockTransactionType.CONVERT,
//                                stockRequestDto.getUpdatedRemark());
//                    }

                avgPrice = calculateAveragePrice(configEntity.getId(),
                        configEntity.getPartEntity().getItemMaster().getId(),
                        configEntity.getPartEntity().getId(),
                        stockRequestDto.getUpdatedBuyPrice(), stockRequestDto.getQty(),
                        stockRequestDto.getUpdatedDetails(), stockRequestDto.getUpdatedRemark());

                responseEntity = updateStockHeaderAndStockDetais(stockHeader, stockRequestDto.getStockType(),
                        stockRequestDto.getQty(), stockRequestDto.getSellPrice(),
                        stockRequestDto.getUpdatedBuyPrice(), user, StockTransactionType.NORMAL,
                        stockRequestDto.getUpdatedRemark());

                stockHeader.setBuyPrice((float) avgPrice);

                updateAverageBuyAndSellPrice((float) avgPrice, stockRequestDto.getSellPrice(), configEntity.getId(),
                        configEntity.getPartEntity().getItemMaster().getId(), configEntity.getPartEntity().getId(),
                        stockRequestDto.getUpdatedDetails(), stockRequestDto.getUpdatedRemark());

                
            } else {

                avgPrice = calculateAveragePrice(configEntity.getId(),
                        configEntity.getPartEntity().getItemMaster().getId(), configEntity.getPartEntity().getId(),
                        stockRequestDto.getUpdatedBuyPrice(), stockRequestDto.getQty(),
                        stockRequestDto.getUpdatedDetails(), stockRequestDto.getUpdatedRemark());

                responseEntity = updateStockHeaderAndStockDetais(updateStockHeaderOp.get(),
                        stockRequestDto.getStockType(),
                        stockHeader.getClosingQty() + stockRequestDto.getQty(), stockRequestDto.getSellPrice(),
                        stockRequestDto.getUpdatedBuyPrice(), user, StockTransactionType.CONVERT,
                        stockRequestDto.getUpdatedRemark());

                stockHeader.setOutQty(stockHeader.getClosingQty());
                stockHeader.setClosingQty(0);

                stockHeader.setBuyPrice((float) avgPrice);

                StockDetails stockDetails = createStockDetails(0, 0, user, 0, stockHeader.getClosingQty());
                stockDetails.setType(StockType.CONVERT.type);
                stockDetails.setRefStockHeaderId(updateStockHeaderOp.get().getId());

                updateStock(stockHeader, stockDetails, StockTransactionType.CONVERT);

                updateAverageBuyAndSellPrice((float) avgPrice, stockRequestDto.getSellPrice(), configEntity.getId(),
                        configEntity.getPartEntity().getItemMaster().getId(), configEntity.getPartEntity().getId(),
                        stockRequestDto.getUpdatedDetails(), stockRequestDto.getUpdatedRemark());
            }
            
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                var t = (StockResponseDto) responseEntity.getBody().getData();
                t.setBuyPrice((float) avgPrice);
                responseEntity.getBody().setData(t);
            }
            return responseEntity;
        } else {
            return createNewStockEntry(stockRequestDto, stockHeader, storageLocationOp.get(), vendorOp.get(),
                    configEntity, user);
        }
    }

    private ResponseEntity<OmsResponse> createNewStockEntry(StockRequestDto stockRequestDto, StockHeader stockHeader,
            StorageLocationEntity storageLocation, VendorEntity vendor,
            ConfigDetailsEntity configEntity, User user) {
        StockHeader stockHeaderNew = StockHeader.builder()
                .openingQty(0)
                .inQty(stockHeader.getClosingQty() + stockRequestDto.getQty())
                .openingQty(0)
                .outQty(0)
                .remark(stockHeader.getRemark())
                .closingQty(stockHeader.getClosingQty() + stockRequestDto.getQty())
                .storageLocation(storageLocation)
                .itemMaster(configEntity.getPartEntity().getItemMaster())
                .partEntity(configEntity.getPartEntity()).configDetailsEntity(configEntity)
                .details(stockRequestDto.getUpdatedDetails())
                .buyPrice(stockRequestDto.getUpdatedBuyPrice())
                .sellPrice(stockRequestDto.getSellPrice())
                .vendor(vendor)
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

    
    
    public ResponseEntity<OmsResponse> updateStockHeaderAndStockDetais(StockHeader stockHeader,
            String stockType, float quantity, float sellPrice, float buyPrice, User user,
            StockTransactionType stockTransactionType, String remark) {
        log.info("update stock header by stockHeader id and stockType");
        StockDetails stockDetails = null;
        switch (StockType.valueOf(stockType.toUpperCase())) {
            case IN: {
                if ((stockHeader.getClosingQty() + quantity) >= 0) {
                    stockHeader.setInQty(stockHeader.getInQty() + quantity);
                    stockHeader.setClosingQty(stockHeader.getClosingQty() + quantity);
                    stockHeader.setSellPrice(sellPrice);
                    if (remark != null && !remark.isEmpty()) {
                        Optional<RemarkEntity> remarkOp = remarkService.findRemarkByRemarkText(remark);
                        if (remarkOp.isPresent()) {
                            stockHeader.setRemark(remarkOp.get().getRemarkText());
                        }
                    } else {
                        stockHeader.setRemark(remark);
                    }
                    stockDetails = createStockDetails(buyPrice, sellPrice, user, quantity, 0);
                    stockDetails.setType(StockType.IN.type);
                } else {
                    return new ResponseEntity<>(
                            OmsResponse.builder().message("Invalid stock quantity received.").build(),
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

    public ResponseEntity<OmsResponse> deleteRowForZerorStock(long stockHeaderId) {
        Optional<StockHeader> stockHeaderOp = stockHeaderRepository.findById(stockHeaderId);
        if(stockHeaderOp.isEmpty()) {
            return new ResponseEntity<>(OmsResponse.builder().message("No daya found to update.").build(),
            HttpStatus.BAD_REQUEST);    
        }
        StockHeader stockHeader = stockHeaderOp.get();
        stockHeader.setRowDelStatus(Boolean.TRUE);

        stockHeaderRepository.save(stockHeader);

        return new ResponseEntity<>(OmsResponse.builder().message("Row deleted successfully.").build(),
                HttpStatus.OK);
    }
}
