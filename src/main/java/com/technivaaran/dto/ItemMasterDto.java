package com.technivaaran.dto;

import org.hibernate.validator.constraints.Length;
import org.springframework.lang.NonNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemMasterDto {

	@Length(max = 50)
	@NonNull
	private String itemName;

	@Length(max = 100)
	private String partNo;

	@Length(max = 10)
	private String status;

	private String configDetails;

}
