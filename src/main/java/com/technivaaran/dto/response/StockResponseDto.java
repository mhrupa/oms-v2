/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.technivaaran.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 *
 * @author MhatreRu
 */
@Data
@Builder
public class StockResponseDto {
    private long id;
    private String box;
    private long boxId;
    private String model;
    private long modelId;
    private String part;
    private long partId;
    private String configuration;
    private long configurationId;
    private String details;
    private long detailsId;
    private float qty;
    private float buyPrice;
    private float sellPrice;
    private String vendor;
    private long vendorId;
    private long stockDetailsId;
    private String remarkText;
    private String stockTransactionType;
}
