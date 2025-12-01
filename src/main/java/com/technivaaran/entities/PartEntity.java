package com.technivaaran.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

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

    @Column(name = "part_no")
    private String partNo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private ItemMaster itemMaster;

    // @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "partEntity", fetch = FetchType.EAGER)
    private List<ConfigDetailsEntity> configurations = new ArrayList<>();
}
