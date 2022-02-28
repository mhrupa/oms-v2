package com.technivaaran.dto;

import org.springframework.lang.NonNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnitMasterDto {
	
	@NonNull
	private String unitName;

	@NonNull
	private String unitCode;
}
