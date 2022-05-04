package com.technivaaran.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class StockRequestDto {
    private long stockHeaderId;
    private String stockType;
    private String boxName;
    private String modelName;
    private long partId;
    private long configId;
    private String details;
    private long vendorId;
    private float qty;
    private float buyPrice;
    private float sellPrice;
    private long userId;
    private String remarkText;

    private String updatedBoxName;
    private String updatedModelName;
    private long updatedPartId;
    private long updatedConfigId;
    private String updatedDetails;
    private long updatedVendorId;
    private float updatedBuyPrice;
    private String updatedRemark;
}
