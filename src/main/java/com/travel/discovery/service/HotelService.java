package com.travel.discovery.service;

import com.travel.discovery.dto.response.HotelResponse;
import com.travel.discovery.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface HotelService {
    PageResponse<HotelResponse> searchHotels(String country, String city,
                                             Integer starRating, Pageable pageable);

    HotelResponse getHotelById(Long id);
}
