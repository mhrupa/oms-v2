package com.technivaaran.enums;

public enum OrderStatus {
    PENDING("Pending"), COMPLETE("Complete"), CANCELLED("Cancelled"), RETURNED("Returned");

    public final String type;

    private OrderStatus(String type) {
        this.type = type;
    }
}
