package com.travel.discovery.service;

import com.travel.discovery.dto.response.HotelResponse;
import com.travel.discovery.dto.response.PageResponse;
import com.travel.discovery.entity.enums.HotelTypeCategory;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface HotelService {
    PageResponse<HotelResponse> searchHotels(String country, String city,
                                             Integer starRating, BigDecimal minPrice,
                                             BigDecimal maxPrice, HotelTypeCategory type,
                                             Pageable pageable);

    HotelResponse getHotelById(Long id);
}
