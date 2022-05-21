package com.technivaaran.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
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

    private float buyPrice;
    private float sellPrice;
    private String details;
    private String remark;
    private long remarkId;

    private long stockDetailId;

    private boolean rowDelStatus;
    
    @ManyToOne
    @JoinColumn(name = "location_id")
    private StorageLocationEntity storageLocation;

    @ManyToOne
    private ItemMaster itemMaster;

    @ManyToOne
    @JoinColumn(name = "part_id")
    private PartEntity partEntity;

    @ManyToOne
    @JoinColumn(name = "config_detail_id")
    private ConfigDetailsEntity configDetailsEntity;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private VendorEntity vendor;
}
