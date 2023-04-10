package com.technivaaran.mapper;

import org.springframework.stereotype.Component;

import com.technivaaran.dto.response.SalesOrderResponseDto;
import com.technivaaran.entities.SalesOrderHeader;
import com.technivaaran.utils.DateUtils;

@Component
public class SalesOrderMapper {

    public SalesOrderResponseDto convertToSalesOrderHeaderToDto(SalesOrderHeader orderHeader) {
        return SalesOrderResponseDto.builder()
                .challanNo(orderHeader.getChallanNo())
                .orderDate(DateUtils.convertDateToddmmyyyy(orderHeader.getOrderDate()))
                .customerName(orderHeader.getCustomer().getCustomerName())
                .customerLocation(orderHeader.getCustomer().getLocation())
                .part(orderHeader.getStockHeader().getPartEntity().getPartNo())
                .partId(orderHeader.getStockHeader().getPartEntity().getId())
                .model(orderHeader.getStockHeader().getItemMaster().getItemName())
                .config(orderHeader.getStockHeader().getConfigDetailsEntity()
                                .getConfiguration())
                .configId(orderHeader.getStockHeader().getConfigDetailsEntity().getId())
                .details(orderHeader.getStockHeader().getDetails())
                .qty(orderHeader.getQuantity())
                .sellPrice(orderHeader.getSellPrice())
                .courierCharges(orderHeader.getCourierCharges())
                .orderAmount(orderHeader.getOrderAmount())
                .salesOrderId(orderHeader.getId())
                .stockHeaderId(orderHeader.getStockHeader().getId())
                .customerId(orderHeader.getCustomer().getId())
                .paymentType(orderHeader.getPaymentType())
                .buyPrice(orderHeader.getStockDetails().getBuyPrice())
                .locationName(orderHeader.getStockHeader().getStorageLocation().getLocationName())
                .build();
    }
}
