package com.ufm.retailsystems.entities.enums;

public enum ERole {
    USER("USER"), ADMIN("ADMIN"), EMPLOYEE("EMPLOYEE");
    private final String text;

    private ERole(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
