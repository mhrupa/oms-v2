package com.technivaaran.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.lang.NonNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDto {

	@NonNull
	private LocalDateTime orderDate;

	@NonNull
	private String poNo;

	@NonNull
	private double orderAmount;

	@NonNull
	private String status;

	@NonNull
	private long customerId;

	@NonNull
	private long userId;
	
	private List<OrderDetailsDto> orderDetailsList;
}
