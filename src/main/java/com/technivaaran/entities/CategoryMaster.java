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
@Table(name = "category_master")
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMaster extends BaseEntity<Integer> {

	@Column(name = "category_name", nullable = false, unique = true)
	private String categoryName;

	@Column(name = "category_code", nullable = false, unique = true)
	private String categoryCode;
	
	@OneToMany(mappedBy = "itemCategory")
	@JsonIgnore
	private List<ItemMaster> items;
}
