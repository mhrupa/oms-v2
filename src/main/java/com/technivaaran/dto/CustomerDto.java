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
	private String firstName;
	
	@Length(max = 50)
	private String lastName;
	
	@Length(max = 50)
	@Email
	private String email;
	
	@Length(max = 20)
	private String contact;
	
	@Length(max = 20)
	private String contact1;
	
	@Length(max = 100)
	private String add1;
	
	@Length(max = 100)
	private String add2;
	
	@Length(max = 50)
	private String city;
	
	@Length(max = 50)
	private String state;
	
	@Length(max = 20)
	private String pincode;
	
	@NonNull
	private int userId;
}
