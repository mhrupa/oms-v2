package com.technivaaran.dto;

import javax.validation.constraints.Email;

import org.hibernate.validator.constraints.Length;
import org.springframework.lang.NonNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDto {

	@Length(max = 50)
	@NonNull
	private String customerName;
	
	@Length(max = 50)
	@Email
	private String email;
	
	@Length(max = 20)
	private String contact;
	
	@Length(max = 20)
	private String location;
	
	@NonNull
	private Integer userId;
}
