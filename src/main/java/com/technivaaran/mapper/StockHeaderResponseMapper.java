package com.technivaaran.mapper;

import com.technivaaran.dto.response.StockResponseDto;
import com.technivaaran.entities.StockHeader;

import org.springframework.stereotype.Component;

/**
 *
 * @author MhatreRu
 */
@Component
public class StockHeaderResponseMapper {

    public StockResponseDto convertToDto(StockHeader stockHeaderEntity) {
        return StockResponseDto.builder()
                .id(stockHeaderEntity.getId())
                .box(stockHeaderEntity.getStorageLocation().getLocationName())
                .boxId(stockHeaderEntity.getStorageLocation().getId())
                .model(stockHeaderEntity.getItemMaster().getItemName())
                .modelId(stockHeaderEntity.getItemMaster().getId())
                .part(stockHeaderEntity.getPartEntity().getPartNo())
                .partId(stockHeaderEntity.getPartEntity().getId())
                .configuration(stockHeaderEntity.getConfigDetailsEntity().getConfiguration())
                .configurationId(stockHeaderEntity.getConfigDetailsEntity().getId())
                .details(stockHeaderEntity.getDetails())
                .detailsId(stockHeaderEntity.getId())
                .qty(stockHeaderEntity.getClosingQty())
                .vendor(stockHeaderEntity.getVendor().getVendorName())
                .vendorId(stockHeaderEntity.getVendor().getId())
                .buyPrice(stockHeaderEntity.getBuyPrice())
                .sellPrice(stockHeaderEntity.getSellPrice())
                .stockDetailsId(stockHeaderEntity.getStockDetailId())
                .build();
    }
}
