package com.technivaaran.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;

import org.hibernate.validator.constraints.Length;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer")
@Builder
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends BaseEntity<Long>{
	
	@Column(name = "first_name", nullable = false)
	@Length(max = 50)
	private String firstName;
	
	@Column(name = "last_name")
	@Length(max = 50)
	private String lastName;
	
	@Column(name = "email", nullable = false, unique = true)
	@Length(max = 50)
	@Email
	private String email;
	
	@Column(name = "contact")
	@Length(max = 20)
	private String contact;
	
	@Column(name = "contact1")
	@Length(max = 20)
	private String contact1;
	
	@Column(name = "add1")
	@Length(max = 100)
	private String add1;
	
	@Column(name = "add2")
	@Length(max = 100)
	private String add2;
	
	@Column(name = "city")
	@Length(max = 50)
	private String city;
	
	@Column(name = "state")
	@Length(max = 50)
	private String state;
	
	@Column(name = "pincode")
	@Length(max = 20)
	private String pincode;
	
	@OneToMany(mappedBy = "customer")
	private List<SalesOrderHeader> salesOrders;
	
	@ManyToOne
	@JsonIgnore
	@NonNull
	private User user;

}
