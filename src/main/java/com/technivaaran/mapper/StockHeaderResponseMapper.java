package com.technivaaran.mapper;

import com.technivaaran.dto.response.StockResponseDto;
import com.technivaaran.entities.StockHeader;
import com.technivaaran.enums.StockTransactionType;

import java.math.BigInteger;

import org.springframework.stereotype.Component;

/**
 *
 * @author MhatreRu
 */
@Component
public class StockHeaderResponseMapper {

    public StockResponseDto convertToDto(StockHeader stockHeaderEntity, StockTransactionType stockTransactionType) {
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
                .remarkText(stockHeaderEntity.getRemark())
                .remarkId(stockHeaderEntity.getRemarkId())
                .stockTransactionType(stockTransactionType.type)
                .build();
    }
    
    public StockResponseDto convertToDto(String[] data, StockTransactionType stockTransactionType) {
        return StockResponseDto.builder().id(Long.parseLong(data[0]))
                .box(data[1]).boxId(Long.parseLong(data[2]))
                .model(data[3]).modelId(Long.parseLong(data[4]))
                .part(data[5]).partId(Long.parseLong(data[6]))
                .configuration(data[7]).configurationId(Long.parseLong(data[8]))
                .details(data[9]).detailsId(Long.parseLong(data[10]))
                .qty(Float.parseFloat(data[11]))
                .vendor(data[12]).vendorId(Long.parseLong(data[13]))
                .buyPrice(Float.parseFloat(data[14])).sellPrice(Float.parseFloat(data[15]))
                .stockDetailsId(Long.parseLong(data[16]))
                .remarkText(data[17]).remarkId(Long.parseLong(data[18]))
                .stockTransactionType(stockTransactionType.type)
                .build();
    }
}
