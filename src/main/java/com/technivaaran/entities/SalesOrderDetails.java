package com.technivaaran.entities;

import java.time.LocalDateTime;

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
@Table(name = "sales_order_details")
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderDetails extends BaseEntity<Long> {

	@Column(name = "order_qty", nullable = false)
	private int orderQty;

	@Column(name = "item_rate", nullable = false)
	private float sellRate;

	@Column(name = "status", nullable = false, columnDefinition = "char(10) DEFAULT 'Active'")
	private String status;

	private LocalDateTime transactionDateTime;

	@ManyToOne
	private SalesOrderHeader salesOrderHeader;

	@ManyToOne
	private User user;

}
