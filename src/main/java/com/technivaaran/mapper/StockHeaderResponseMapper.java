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
                .model(stockHeaderEntity.getItemMaster().getItemName())
                .part(stockHeaderEntity.getPartEntity().getPartNo())
                .configuration(stockHeaderEntity.getConfigDetailsEntity().getConfiguration())
                .details(stockHeaderEntity.getDetails())
                .qty(stockHeaderEntity.getClosingQty())
                .build();
    }
}
