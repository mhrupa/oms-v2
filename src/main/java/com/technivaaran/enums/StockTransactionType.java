package com.technivaaran.enums;

public enum StockTransactionType {
    CONVERT("Convert"), NORMAL("Normal");

    public final String type;

    private StockTransactionType(String type) {
        this.type = type;
    }
}
