package com.technivaaran.enums;

public enum PaymentType {
    CASH("Cash"), BANK("Bank"),PAYTM("Paytm"), PENDING("Pending"), VPP("vpp"), RETURNED("returned");

    public final String type;

    private PaymentType(String type) {
        this.type = type;
    }
}
