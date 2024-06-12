package com.ufm.retailsystems.entities.enums;

public enum Status {
    PENDING("PENDING"), DISPATCHED("DISPATCHED"), IN_TRANSIT("IN_TRANSIT"), DELIVERED("DELIVERED"), RETURNED("RETURNED");
    private final String text;

    private Status(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
