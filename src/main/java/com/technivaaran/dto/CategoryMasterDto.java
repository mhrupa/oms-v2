package com.technivaaran.dto;

import org.springframework.lang.NonNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryMasterDto {
	
	@NonNull
	private String categoryName;
	
	@NonNull
	private String categoryCode;
}
