package com.technivaaran.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	
	@OneToMany(mappedBy = "itemUnit")
	@JsonIgnore
	private List<ItemMaster> items;
}
