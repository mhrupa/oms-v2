package com.technivaaran.dto;

import org.hibernate.validator.constraints.Length;
import org.springframework.lang.NonNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BranchMasterDto {
	@Length(max = 50)
	@NonNull
	private String branchName;

	@Length(max = 15)
	@NonNull
	private String branchCode;

	@Length(max = 250)
	private String branchAdd;

	private boolean homeBranch;
}
