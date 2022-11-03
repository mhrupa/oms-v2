package com.technivaaran.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Email;

import org.hibernate.validator.constraints.Length;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
	@Length(max = 50)
	@NonNull
	private String userName;

	@Length(max = 50)
	@NonNull
	@Email
	private String email;
	
	@Length(max = 50)
	@NonNull
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	
	private String status;
	
	private LocalDateTime lastLoginOn;
	
	private boolean firstLogin;

	@NonNull
	private Integer branchId;
	
	@NonNull
	private Integer roleId;
}
