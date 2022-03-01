package com.technivaaran.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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

    @Column(name = "sku_name", nullable = false)
    private String skuName;

    @Column(name = "part_no")
    private String partNo;

    @Column(name = "opening_qty", nullable = false)
    private float openingQty;

    @Column(name = "in_qty", nullable = false)
    private float inQty;

    @Column(name = "out_qty", nullable = false)
    private float outQty;

    @Column(name = "closing_qty", nullable = false)
    private float closingQty;

}
