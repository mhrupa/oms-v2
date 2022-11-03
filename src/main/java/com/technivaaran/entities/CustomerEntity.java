package com.technivaaran.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.validator.constraints.Length;
import org.springframework.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer")
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity extends BaseEntity<Long> {

	@Column(name = "customer_name")
	@Length(max = 50)
	private String customerName;

	@Column(name = "email", nullable = false)
	@Length(max = 50)
	@Email
	private String email;

	@Column(name = "contact")
	@Length(max = 50)
	private String contact;

	@Column(name = "location")
	@Length(max = 20)
	private String location;

	@ManyToOne
	@JsonIgnore
	@NonNull
	private User user;

}
