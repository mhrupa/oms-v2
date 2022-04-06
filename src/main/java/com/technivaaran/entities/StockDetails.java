package com.technivaaran.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stock_details")
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class StockDetails extends BaseEntity<Long> {

    @CreationTimestamp
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "in_qty", nullable = false)
    private float inQty;

    @Column(name = "out_qty", nullable = false)
    private float outQty;

    private float buyPrice;
    private float sellPrice;

    private String type;

    @Column(columnDefinition = "BIGINT(20) DEFAULT 0")
    private long refStockHeaderId;

    @ManyToOne
    private StockHeader stockHeader;

    @ManyToOne
    @JsonIgnore
    private User user;
}
