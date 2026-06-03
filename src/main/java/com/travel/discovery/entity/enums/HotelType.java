package com.travel.discovery.entity.enums;

/**
 * High-level accommodation type for a property, mirroring Booking.com's
 * accommodation-type categories collapsed to the handful we surface in the app.
 *
 * <p>The {@code searchHotels} payload carries no accommodation-type field, so the
 * type is derived from the free-text {@code accessibilityLabel} via
 * {@link #fromLabel(String)}. Booking tags whole-unit rentals as
 * {@code "Entire <type>"} (apartment / villa / chalet / home); everything else is
 * a room-based listing and is treated as a {@link #HOTEL}.
 */
public enum HotelType {
    HOTEL,
    APARTMENT,
    RESORT,
    VILLA,
    GUEST_HOUSE,
    HOLIDAY_HOME,
    HOSTEL,
    MOTEL,
    BED_AND_BREAKFAST,
    CHALET,
    APART_HOTEL,
    OTHER;

    /**
     * Best-effort accommodation type from Booking's {@code accessibilityLabel}.
     * A blank label means we have nothing to go on ({@link #OTHER}); anything
     * unrecognised defaults to a room-based listing ({@link #HOTEL}).
     */
    public static HotelType fromLabel(String label) {
        if (label == null || label.isBlank()) return OTHER;
        String l = label.toLowerCase();

        if (l.contains("apart-hotel") || l.contains("aparthotel"))          return APART_HOTEL;
        if (l.contains("apartment") || l.contains("studio"))                return APARTMENT;
        if (l.contains("villa"))                                            return VILLA;
        if (l.contains("chalet"))                                           return CHALET;
        if (l.contains("home") || l.contains("house")
                || l.contains("bungalow") || l.contains("cottage"))         return HOLIDAY_HOME;

        return HOTEL;
    }
}