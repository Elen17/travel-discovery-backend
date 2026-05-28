package com.travel.discovery.service;

import com.travel.discovery.dto.response.HotelResponse;
import com.travel.discovery.dto.response.PageResponse;
import com.travel.discovery.dto.response.ReviewResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface HotelService {
    PageResponse<HotelResponse> searchHotels(String country, String city,
        BigDecimal minPrice, BigDecimal maxPrice, Integer starRating, Pageable pageable);
    HotelResponse getHotelById(Long id);
    PageResponse<ReviewResponse> getHotelReviews(Long hotelId, Pageable pageable);
}
