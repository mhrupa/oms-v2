package com.technivaaran.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "branch_master")
@Builder
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
public class BranchMaster extends BaseEntity<Integer>{

	@Column(name = "branch_name", nullable = false, unique = true)
	@Length(max = 50)
	private String branchName;
	
	@Column(name = "branch_code", nullable = false, unique = true)
	@Length(max = 15)
	private String branchCode;
	
	@Length(max = 250)
	@Column(name = "branch_add")
	private String branchAdd;
	
	@Column(name = "home_branch", nullable = false, columnDefinition = "tinyint(1) default 0")
	private boolean homeBranch;
	
	@OneToMany(mappedBy = "userBranch")
	@JsonIgnore
	private List<User> users;
	
}

