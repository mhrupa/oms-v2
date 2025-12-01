package com.technivaaran.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "unit_master")
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UnitMaster extends BaseEntity<Integer>{
	
	@Column(name = "unit_name", nullable = false, unique = true)
	private String unitName;
	
	@Column(name = "unit_code", nullable = false, unique = true)
	private String unitCode;
	
}
