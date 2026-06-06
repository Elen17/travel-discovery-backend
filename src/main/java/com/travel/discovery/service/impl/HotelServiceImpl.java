package com.travel.discovery.service.impl;

import com.travel.discovery.dto.response.HotelResponse;
import com.travel.discovery.dto.response.PageResponse;
import com.travel.discovery.entity.Hotel;
import com.travel.discovery.entity.enums.HotelTypeCategory;
import com.travel.discovery.exception.ResourceNotFoundException;
import com.travel.discovery.repository.HotelRepository;
import com.travel.discovery.mapper.HotelMapper;
import com.travel.discovery.service.HotelService;
import com.travel.discovery.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;
    private final LocationService locationService;

    @Override
    public PageResponse<HotelResponse> searchHotels(String country, String city,
        Integer starRating, BigDecimal minPrice, BigDecimal maxPrice, HotelTypeCategory type,
        Pageable pageable) {

        // Reject unknown country/city before querying.
        locationService.validateLocation(country, city);

        // The filter arrives as a type-safe category; the query matches on the
        // hotel_type row name it maps to.
        String typeName = type != null ? type.getName() : null;

        Page<Hotel> page = hotelRepository.searchHotels(
            country, city, starRating, minPrice, maxPrice, typeName, pageable);

        return PageResponse.<HotelResponse>builder()
            .content(page.getContent().stream().map(hotelMapper::toResponse).toList())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }

    @Override
    public HotelResponse getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Hotel", id));
        return hotelMapper.toResponse(hotel);
    }
}