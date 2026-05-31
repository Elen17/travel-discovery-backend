package com.travel.discovery.service;

import com.travel.discovery.dto.response.HotelResponse;

import java.util.List;

public interface FavouritesService {

    List<HotelResponse> getFavourites(Long userId);

    /** Adds a hotel to the user's favourites; no-op-safe duplicate check throws 409. */
    void addToFavourites(Long userId, Long hotelId);

    /** Removes a hotel from the user's favourites (idempotent — missing entry is a no-op). */
    void removeFromFavourites(Long userId, Long hotelId);
}