package com.technivaaran.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sales_order_header")
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderHeader extends BaseEntity<Long> {

	@NonNull
	@Column(name = "order_date")
	private LocalDate orderDate;

	private Long challanNo;
	private float sellPrice;
	private long quantity;
	private float courierCharges;
	private String paymentType;
	private String remark;

	@Column(name = "order_amount", nullable = false)
	private double orderAmount;

	@Column(name = "status", nullable = false, columnDefinition = "char(10) DEFAULT 'Active'")
	private String status;

	@OneToOne
	private StockDetails stockDetails;

	@ManyToOne
	private CustomerEntity customer;

	@ManyToOne(fetch = FetchType.EAGER)
	private StockHeader stockHeader;

	@ManyToOne
	@JsonIgnore
	private User user;
}
