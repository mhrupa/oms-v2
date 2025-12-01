package com.technivaaran.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@MappedSuperclass
public class BaseEntity<T> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private T id;

	@Column(name = "created_at")
	@CreationTimestamp
	@JsonIgnore
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	@UpdateTimestamp
	@JsonIgnore
	private LocalDateTime updatedAt;

	@Version
	@JsonIgnore
	private long version;
}
