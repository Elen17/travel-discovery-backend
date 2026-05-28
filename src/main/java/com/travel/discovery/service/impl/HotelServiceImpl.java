package com.travel.discovery.service.impl;

import com.travel.discovery.dto.response.HotelResponse;
import com.travel.discovery.dto.response.PageResponse;
import com.travel.discovery.dto.response.ReviewResponse;
import com.travel.discovery.entity.Hotel;
import com.travel.discovery.exception.ResourceNotFoundException;
import com.travel.discovery.repository.HotelRepository;
import com.travel.discovery.repository.ReviewRepository;
import com.travel.discovery.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public PageResponse<HotelResponse> searchHotels(String country, String city,
        BigDecimal minPrice, BigDecimal maxPrice, Integer starRating, Pageable pageable) {

        Page<Hotel> page = hotelRepository.searchHotels(country, city, minPrice, maxPrice, starRating, pageable);

        return PageResponse.<HotelResponse>builder()
            .content(page.getContent().stream().map(this::toResponse).toList())
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
        return toResponse(hotel);
    }

    @Override
    public PageResponse<ReviewResponse> getHotelReviews(Long hotelId, Pageable pageable) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new ResourceNotFoundException("Hotel", hotelId);
        }
        var page = reviewRepository.findByHotelId(hotelId, pageable);
        return PageResponse.<ReviewResponse>builder()
            .content(page.getContent().stream().map(r -> ReviewResponse.builder()
                .id(r.getId())
                .reviewerName(r.getUser().getFullName())
                .reviewerAvatarUrl(r.getUser().getAvatarUrl())
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt())
                .build()).toList())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }

    private HotelResponse toResponse(Hotel hotel) {
        Double avgRating = reviewRepository.findAverageRatingByHotelId(hotel.getId()).orElse(0.0);
        long reviewCount = reviewRepository.countByHotelId(hotel.getId());

        return HotelResponse.builder()
            .id(hotel.getId())
            .name(hotel.getName())
            .description(hotel.getDescription())
            .country(hotel.getCountry())
            .city(hotel.getCity())
            .address(hotel.getAddress())
            .latitude(hotel.getLatitude())
            .longitude(hotel.getLongitude())
            .pricePerNight(hotel.getPricePerNight())
            .starRating(hotel.getStarRating())
            .mainImageUrl(hotel.getMainImageUrl())
            .isFeatured(hotel.getIsFeatured())
            .amenities(hotel.getAmenities().stream().map(Enum::name).collect(Collectors.toSet()))
            .imageUrls(hotel.getImages().stream().map(img -> img.getImageUrl()).toList())
            .averageRating(Math.round(avgRating * 10.0) / 10.0)
            .reviewCount(reviewCount)
            .build();
    }
}
