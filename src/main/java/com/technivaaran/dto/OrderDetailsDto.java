package com.technivaaran.dto;

import org.springframework.lang.NonNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDetailsDto {
	
	@NonNull
	private int orderQty;

	@NonNull
	private double itemRate;

	@NonNull
	private long orderItemId;
	
	@NonNull
	private long userId;
}
