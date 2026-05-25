package com.traveldiscovery.service;

import com.traveldiscovery.dto.response.HotelResponse;
import com.traveldiscovery.dto.response.PageResponse;
import com.traveldiscovery.dto.response.ReviewResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface HotelService {
    PageResponse<HotelResponse> searchHotels(String country, String city,
        BigDecimal minPrice, BigDecimal maxPrice, Integer starRating, Pageable pageable);
    HotelResponse getHotelById(Long id);
    PageResponse<ReviewResponse> getHotelReviews(Long hotelId, Pageable pageable);
}
