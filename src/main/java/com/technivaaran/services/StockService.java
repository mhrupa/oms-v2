package com.technivaaran.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.technivaaran.dto.OmsResponse;
import com.technivaaran.dto.request.StockRequestDto;
import com.technivaaran.dto.response.StockResponseDto;
import com.technivaaran.entities.ConfigDetailsEntity;
import com.technivaaran.entities.ItemMaster;
import com.technivaaran.entities.PartEntity;
import com.technivaaran.entities.StockDetails;
import com.technivaaran.entities.StockHeader;
import com.technivaaran.entities.StorageLocationEntity;
import com.technivaaran.entities.User;
import com.technivaaran.entities.VendorEntity;
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

    @Transactional
    public ResponseEntity<OmsResponse> createStockEntry(StockRequestDto stockRequestDto) {
        Optional<ConfigDetailsEntity> configOp = configDetailsService.findById(stockRequestDto.getConfigId());
        if (configOp.isEmpty()) {
            return new ResponseEntity<>(OmsResponse.builder().message("Invalid configuration received.").build(),
                    HttpStatus.BAD_REQUEST);
        }
        Optional<StorageLocationEntity> storageLocationOp = storageLocationService.findByLocationName(stockRequestDto.getBoxName());
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
                storageLocationOp.get(), configEntity.getPartEntity().getItemMaster(), configEntity.getPartEntity(),
                configEntity, vendorOp.get(), stockRequestDto.getBuyPrice());

        StockHeader stockHeader = null;
        if (stockHeaderOp.isPresent()) {
            return new ResponseEntity<>(
                    OmsResponse.builder().message("Stock data already available click on table row to update data.")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        } else {
            stockHeader = StockHeader.builder()
                    .openingQty(0)
                    .inQty(stockRequestDto.getQty())
                    .openingQty(0)
                    .outQty(0)
                    .closingQty(stockRequestDto.getQty())
                    .storageLocation(storageLocationOp.get())
                    .itemMaster(configEntity.getPartEntity().getItemMaster())
                    .partEntity(configEntity.getPartEntity()).configDetailsEntity(configEntity)
                    .details(stockRequestDto.getDetails())
                    .buyPrice(stockRequestDto.getBuyPrice())
                    .sellPrice(stockRequestDto.getSellPrice())
                    .vendor(vendorOp.get())
                    .build();
            stockHeaderRepository.save(stockHeader);

            User user = userService.getUserById(stockRequestDto.getUserId());

            StockDetails stockDetails = createStockDetails(stockRequestDto.getBuyPrice(),
                    stockRequestDto.getSellPrice(), user);
            stockDetails.setInQty(stockRequestDto.getQty());
            stockDetails.setOutQty(0);
            stockDetails.setStockHeader(stockHeader);
            stockDetailsRepository.save(stockDetails);

            stockHeader.setStockDetailId(stockDetails.getId());
            stockHeader = stockHeaderRepository.save(stockHeader);

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

    public ResponseEntity<OmsResponse> updateStockHeaderAndStockDetaisById(long id, StockRequestDto stockRequestDto) {
        Optional<StockHeader> stockHeaderOp = stockHeaderRepository.findById(id);
        if (stockHeaderOp.isPresent()) {
            User user = userService.getUserById(stockRequestDto.getUserId());
            return updateStockHeaderAndStockDetais(stockHeaderOp.get(), stockRequestDto.getStockType(),
                    stockRequestDto.getQty(), stockRequestDto.getSellPrice(), stockHeaderOp.get().getBuyPrice(),
                    user);
        } else {
            return new ResponseEntity<>(OmsResponse.builder().message("Invalid stock header received.").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<OmsResponse> updateStockHeaderAndStockDetais(StockHeader stockHeader,
            String stockType, int quantity, float sellPrice, float buyPrice, User user) {
        log.info("update stock header by stockHeader id and stockType");
        StockDetails stockDetails = null;
        switch (StockType.valueOf(stockType.toUpperCase())) {
            case IN: {
                if ((stockHeader.getClosingQty() + quantity) >= 0) {
                    stockHeader.setInQty(stockHeader.getInQty() + quantity);
                    stockHeader.setClosingQty(stockHeader.getClosingQty() + quantity);
                    stockHeader.setSellPrice(sellPrice);

                    stockDetails = createStockDetails(buyPrice, sellPrice, user);
                    stockDetails.setInQty(quantity);
                    stockDetails.setOutQty(0);
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

                stockDetails = createStockDetails(buyPrice, sellPrice, user);
                stockDetails.setInQty(0);
                stockDetails.setOutQty(quantity);
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
        return updateStock(stockHeader, stockDetails);
    }

    private ResponseEntity<OmsResponse> updateStock(StockHeader stockHeader, StockDetails stockDetails) {
        stockHeader = stockHeaderRepository.save(stockHeader);

        stockDetails.setStockHeader(stockHeader);
        stockDetails = stockDetailsRepository.save(stockDetails);

        stockHeader.setStockDetailId(stockDetails.getId());
        stockHeader = stockHeaderRepository.save(stockHeader);

        return new ResponseEntity<>(OmsResponse.builder().message("Stock updated successfully.")
                .data(stockHeaderResponseMapper.convertToDto(stockHeader)).build(), HttpStatus.OK);
    }

    private StockDetails createStockDetails(float buyPrice,
            float sellPrice, User user) {
        return StockDetails.builder()
                .transactionDate(LocalDateTime.now())
                .buyPrice(buyPrice)
                .sellPrice(sellPrice)
                .user(user)
                .build();

    }

    public Optional<StockHeader> getStockHeaderByStockHeaderId(long stockHeaderId) {
        return stockHeaderRepository.findById(stockHeaderId);
    }

    public Optional<StockDetails> findStockDetailsById(long stockDetailId) {
        return stockDetailsRepository.findById(stockDetailId);
    }
}
