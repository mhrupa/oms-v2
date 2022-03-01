package com.technivaaran.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item_master")
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ItemMaster extends BaseEntity<Long> {

	@Column(name = "item_name", nullable = false, unique = true)
	@Length(max = 50)
	private String itemName;

	@Column(name = "part_no")
	@Length(max = 100)
	private String partNo;

	@Column(name = "status", columnDefinition = "char(10) default 'Active'")
	private String status;

	@ManyToOne
	// @JsonIgnore
	private UnitMaster itemUnit;

}
