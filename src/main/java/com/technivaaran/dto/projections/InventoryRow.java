package com.technivaaran.dto.projections;

import java.math.BigDecimal;

public record InventoryRow(
        Long id,
        String locationName,
        Integer locationId,
        String modelName,
        Long modelId,
        String partNo,
        Long partId,
        String configuration,
        Long configurationId,
        String details,
        Long detailId,
        Float qty,
        String vendorName,
        Long vendorId,
        Float buyPrice,
        Float sellPrice,
        Long stockDetailId,
        String remark,
        Long remarkId,
        String stockTransactionType) {
}
