package com.travel.discovery.entity.enums;

public enum

PlannerMessageRole {
    USER,
    ASSISTANT;

    /**
     * Case-insensitive lookup used when mapping the frontend's lowercase role
     * (e.g. "user", "assistant"). Defaults to USER for null/blank input.
     */
    public static PlannerMessageRole fromValue(String value) {
        if (value == null || value.isBlank()) {
            return USER;
        }
        for (PlannerMessageRole r : values()) {
            if (r.name().equalsIgnoreCase(value.trim())) {
                return r;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + value);
    }

    /** Lowercase wire value expected by the frontend. */
    public String toValue() {
        return name().toLowerCase();
    }
}
