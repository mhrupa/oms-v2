package com.technivaaran.dto;

import org.hibernate.validator.constraints.Length;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemMasterDto {

	@Length(max = 50)
	private String itemName;

	private long itemId;

	@Length(max = 100)
	private String partNo;

	@Length(max = 100)
	private long partId;

	@Length(max = 10)
	private String status;

	private String configDetails;

}
