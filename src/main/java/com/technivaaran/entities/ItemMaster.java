package com.technivaaran.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

	@JsonIgnore
	@Column(name = "status", columnDefinition = "char(10) default 'Active'")
	private String status;

	// @JsonIgnore
	@Builder.Default
	@OneToMany(mappedBy = "itemMaster", fetch = FetchType.EAGER)
	private List<PartEntity> parts = new ArrayList<>();
}
