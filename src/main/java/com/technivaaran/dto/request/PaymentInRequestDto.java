package com.technivaaran.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class PaymentInRequestDto {
    private String challanNos;
    private long customerId;
    private String paymentType;
    private String paymentAccount;
    private double amount;
    private double updatedAmount;
    private long userId;
    private String paymentDate;
}
