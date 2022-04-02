package com.technivaaran.entities;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
