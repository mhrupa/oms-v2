package com.technivaaran.enums;

public enum StockType {
    IN("In"), OUT("Out"), RETURN("Return"), TRANSFER("Transfer"), CONVERT("Convert");

    public final String type;

    private StockType(String type) {
        this.type = type;
    }
}
