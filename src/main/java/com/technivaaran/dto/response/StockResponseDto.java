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
    private String model;
    private String part;
    private String configuration;
    private String details;
    private float qty;
    private float buyPrice;
    private float sellPrice;
    private String remark1;
    private String remark2;
    
}
