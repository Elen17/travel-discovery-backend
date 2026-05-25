package com.traveldiscovery.service;

import com.traveldiscovery.dto.response.HotelResponse;
import java.util.List;

public interface WishlistService {
    List<HotelResponse> getWishlist(String email);
    void addToWishlist(String email, Long hotelId);
    void removeFromWishlist(String email, Long hotelId);
}
