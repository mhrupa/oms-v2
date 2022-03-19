package com.technivaaran.entities;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

	@CreationTimestamp
	@NonNull
	@Column(name = "order_date")
	private LocalDateTime orderDate;

	@Column(name = "po_no", nullable = false)
	private String poNo;

	@Column(name = "order_amount", nullable = false)
	private double orderAmount;

	@Column(name = "status", nullable = false, columnDefinition = "char(10) DEFAULT 'Active'")
	private String status;

	@ManyToOne
	private CustomerEntity customer;

	@OneToMany(mappedBy = "salesOrderHeader")
	private List<SalesOrderDetails> orderDetails;

	@ManyToOne
	@JsonIgnore
	private User user;
}
