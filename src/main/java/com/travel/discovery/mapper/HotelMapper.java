package com.travel.discovery.mapper;

import com.travel.discovery.dto.response.HotelResponse;
import com.travel.discovery.entity.Hotel;
import com.travel.discovery.entity.HotelImage;
import com.travel.discovery.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Single place that turns a persisted {@link Hotel} into the API-facing
 * {@link HotelResponse}, including the average rating and review count computed
 * from our own reviews table. Shared by the catalog and search stacks so both
 * return an identical shape (with the bookable Long id).
 */
@Component
@RequiredArgsConstructor
public class HotelMapper {

    private final ReviewRepository reviewRepository;

    public HotelResponse toResponse(Hotel hotel) {
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
            .imageUrls(hotel.getImages().stream().map(HotelImage::getImageUrl).toList())
            .averageRating(Math.round(avgRating * 10.0) / 10.0)
            .reviewCount(reviewCount)
            .build();
    }
}