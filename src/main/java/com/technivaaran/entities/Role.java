package com.technivaaran.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "roles")
@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity<Integer>{

	@Column(name = "role_name")
	private String roleName;
	
	@Column(name = "role_code")
	private String roleCode;
	
	@OneToMany(mappedBy = "userRole")
	@JsonIgnore
	private List<User> users;
}