package com.javatv.enums;

/**
 * Televizyonun güç durumunu temsil eder.
 */
public enum PowerState {
    OFF("Kapalı"),
    ON("Açık"),
    STANDBY("Bekleme Modu");

    private final String description;

    PowerState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
