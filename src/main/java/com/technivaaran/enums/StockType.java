package com.technivaaran.enums;

public enum StockType {
    IN("In"), OUT("Out"), RETURN("Return");

    public final String type;

    private StockType(String type) {
        this.type = type;
    }
}
