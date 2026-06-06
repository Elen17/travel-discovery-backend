package com.travel.discovery.entity.enums;

import lombok.Getter;

/**
 * In-code handle for the rows of the {@code user_role} reference table (see
 * {@link com.travel.discovery.entity.UserRole}). The {@link #getId() id} matches
 * the seeded primary key, so it can resolve the entity without a name lookup.
 */
@Getter
public enum UserRole {
    ADMIN(1),
    USER(2);

    private final int id;

    UserRole(int id) {
        this.id = id;
    }
}