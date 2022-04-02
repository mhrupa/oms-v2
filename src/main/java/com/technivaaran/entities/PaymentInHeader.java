package com.technivaaran.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_in_header")
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInHeader  extends BaseEntity<Long> {

    @Column(name = "payment_in_date", nullable = false)
	private LocalDate paymentInDate;

    private double amount;

    private String paymentType;

    private String paymentAccountName;

    @ManyToOne
    private CustomerEntity customer;

    @ManyToOne
    private User user;
}
