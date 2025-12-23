package com.technivaaran.ws;

public class UiEvent {
    private String type; // e.g. "MASTERDATA_CHANGED", "NOTIFICATION_NEW"
    private String entity; // e.g. "CUSTOMER", "VENDOR", "PAYMENT_OPTION"
    private String action; // e.g. "CHANGED", "UPSERT", "DELETE"
    private String id; // optional (string to keep generic)
    private long ts;

    public UiEvent() {
    }

    public UiEvent(String type, String entity, String action, String id, long ts) {
        this.type = type;
        this.entity = entity;
        this.action = action;
        this.id = id;
        this.ts = ts;
    }

    public static UiEvent upsert(String entity, String id) {
        return new UiEvent("MASTERDATA_CHANGED", entity, "UPSERT", id, System.currentTimeMillis());
    }

    public static UiEvent upsert(String entity) {
        return new UiEvent("MASTERDATA_CHANGED", entity, "UPSERT", null, System.currentTimeMillis());
    }

    public static UiEvent changed(String entity) {
        return new UiEvent("MASTERDATA_CHANGED", entity, "CHANGED", null, System.currentTimeMillis());
    }

    // getters/setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }
}
