package com.technivaaran.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class OrderRequestDto {
    private long orderId;
    private String orderDate;
	private long challanNo;
	private float sellPrice;
	private int quantity;
	private float courierCharges;
	private String paymentType;
	private String remark;
    private long customerId;
    private long stockHeaderId;
    private long stockDetailId;
    private long userId;
}
