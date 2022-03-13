package com.technivaaran.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "part_details")
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PartEntity extends BaseEntity<Long> {

    @Column(name = "part_no", unique = true)
    private String partNo;

    @JsonIgnore
    @ManyToOne
    private ItemMaster itemMaster;

    // @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "partEntity")
    private List<ConfigDetailsEntity> configurations = new ArrayList<>();
}
