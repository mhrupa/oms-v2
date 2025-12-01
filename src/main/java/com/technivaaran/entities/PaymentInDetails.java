package com.technivaaran.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_in_details")
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInDetails extends BaseEntity<Long> {

    private long challanNo;

    private double orderAmount;

    private LocalDate transactionDate;

    @ManyToOne
    private PaymentInHeader paymentInHeader;
}
