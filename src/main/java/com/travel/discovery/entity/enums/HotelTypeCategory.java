package com.travel.discovery.entity.enums;

import lombok.Getter;

/**
 * In-code classification of accommodation types, mirroring the rows of the
 * {@code hotel_type} reference table (see
 * {@link com.travel.discovery.entity.HotelType}). Each constant carries the
 * {@link #getName() display name} used to resolve the matching table row.
 *
 * <p>The {@code searchHotels} payload carries no accommodation-type field, so the
 * type is derived from the free-text {@code accessibilityLabel} via
 * {@link #fromLabel(String)}. Whole-unit rentals Booking tags as
 * {@code "Entire <type>"} (home / chalet / apart-hotel / B&amp;B) that we no
 * longer model as first-class types collapse to {@link #OTHER}.
 */
@Getter
public enum HotelTypeCategory {
    HOTEL("Hotel"),
    VILLA("Villa"),
    APARTMENT("Apartment"),
    RESORT("Resort"),
    GUEST_HOUSE("Guest House"),
    HOSTEL("Hostel"),
    MOTEL("Motel"),
    OTHER("Other");

    /**
     * -- GETTER --
     * Matches the
     *  of the corresponding
     *  row.
     */
    private final String name;

    HotelTypeCategory(String name) {
        this.name = name;
    }

    /**
     * Lenient lookup for request-param binding: case-insensitive and accepting
     * either the constant name ({@code GUEST_HOUSE}) or the display name
     * ({@code "Guest House"}). Blank input yields {@code null} (no filter).
     */
    public static HotelTypeCategory fromParam(String value) {
        if (value == null || value.isBlank()) return null;
        String normalized = value.trim().replace(' ', '_').toUpperCase();
        for (HotelTypeCategory category : values()) {
            if (category.name().equals(normalized)) return category;
        }
        throw new IllegalArgumentException("Unknown hotel type: " + value);
    }

    /**
     * Best-effort accommodation type from Booking's {@code accessibilityLabel}.
     * A blank label means we have nothing to go on ({@link #OTHER}); anything
     * unrecognised defaults to a room-based listing ({@link #HOTEL}).
     */
    public static HotelTypeCategory fromLabel(String label) {
        if (label == null || label.isBlank()) return OTHER;
        String l = label.toLowerCase();

        if (l.contains("apart-hotel") || l.contains("aparthotel"))   return OTHER;
        if (l.contains("apartment") || l.contains("studio"))         return APARTMENT;
        if (l.contains("villa"))                                     return VILLA;
        if (l.contains("resort"))                                    return RESORT;
        if (l.contains("guest house") || l.contains("guesthouse"))   return GUEST_HOUSE;
        if (l.contains("hostel"))                                    return HOSTEL;
        if (l.contains("motel"))                                     return MOTEL;
        if (l.contains("chalet") || l.contains("home")
                || l.contains("house") || l.contains("bungalow")
                || l.contains("cottage"))                            return OTHER;

        return HOTEL;
    }
}