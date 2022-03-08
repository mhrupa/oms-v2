package com.technivaaran.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stock_header")
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class StockHeader extends BaseEntity<Long> {

    @Column(name = "location")
    private String location;

    @Column(name = "stock_date")
	@JsonIgnore
	private LocalDate stockDate;

    @Column(name = "opening_qty", nullable = false)
    private float openingQty;

    @Column(name = "in_qty", nullable = false)
    private float inQty;

    @Column(name = "out_qty", nullable = false)
    private float outQty;

    @Column(name = "closing_qty", nullable = false)
    private float closingQty;

    @ManyToOne
    private ItemMaster itemMaster;

}
