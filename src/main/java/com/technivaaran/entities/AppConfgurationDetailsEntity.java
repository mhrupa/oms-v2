package com.technivaaran.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "app_config_details")
@Builder
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
public class AppConfgurationDetailsEntity extends BaseEntity<Integer> {
    
    private String parameterName;
    private String parameterValue;
}
