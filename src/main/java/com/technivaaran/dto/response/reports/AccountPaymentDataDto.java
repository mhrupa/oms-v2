package com.technivaaran.dto.response.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountPaymentDataDto {
    private String challanNo;
    private String orderDate;
    private String paymentDate;
    private String customerName;
    private String itemName;
    private String orderAmount;
}
