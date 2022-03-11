package com.technivaaran.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
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

	@Column(name = "status", columnDefinition = "char(10) default 'Active'")
	private String status;

	// @JsonIgnore
	@OneToMany(mappedBy = "itemMaster", fetch = FetchType.EAGER)
	private List<PartEntity> parts;
}
