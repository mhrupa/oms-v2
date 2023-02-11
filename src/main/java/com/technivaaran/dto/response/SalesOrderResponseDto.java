package com.technivaaran.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalesOrderResponseDto {
    private long challanNo;
    private String orderDate;
    private String customerName;
    private String customerLocation;
    private String model;
    private String part;
    private String config;
    private String details;
    private long qty;
    private float sellPrice;
    private float courierCharges;
    private double orderAmount;
    private long salesOrderId;
    private long stockHeaderId;
    private long customerId;
    private String paymentType;
    private String paymentAccName;
    private String paymentDate;
    private long partId;
    private long configId;
    private float buyPrice;
    private String locationName;
}
