package com.technivaaran.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class StockRequestDto {
    private int boxId;
    private long modelId;
    private long partId;
    private long configId;
    private String details;
    private String vendor;
    private int qty;
    private float buyPrice;
    private float sellPrice;
    private String remark1;
    private String remark2;
}
