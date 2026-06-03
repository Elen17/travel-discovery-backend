package com.travel.discovery.mapper;

import com.travel.discovery.dto.response.HotelResponse;
import com.travel.discovery.entity.Hotel;
import com.travel.discovery.entity.HotelImage;
import com.travel.discovery.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Single place that turns a persisted {@link Hotel} into the API-facing
 * {@link HotelResponse}, including the average rating and review count computed
 * from our own reviews table. Shared by the catalog and search stacks so both
 * return an identical shape (with the bookable Long id).
 */
@Component
public class HotelMapper {

    private final ReviewRepository reviewRepository;

    /** Every hotel gallery is topped up to at least this many photos. */
    private static final int MIN_GALLERY_IMAGES = 3;

    /**
     * Curated, royalty-free facility photos (pool, gym, restaurant, spa, room,
     * lobby, bar) used to fill galleries for hotels that have fewer than
     * {@link #MIN_GALLERY_IMAGES} of their own images. Extended at startup by
     * any URLs supplied via the {@code hotel.fallback-photos} property.
     */
    private static final List<String> DEFAULT_FALLBACK_PHOTOS = List.of(
        "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=600", // pool
        "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=600", // pool
        "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=600", // restaurant
        "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=600", // restaurant
        "https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=600", // room
        "https://images.unsplash.com/photo-1582719508461-905c673771fd?w=600", // room
        "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600", // lobby
        "https://images.unsplash.com/photo-1514933651103-005eec06c04b?w=600"  // bar
    );

    /** Built-in defaults plus any URLs configured via {@code hotel.fallback-photos}. */
    private final List<String> fallbackPhotos;

    public HotelMapper(ReviewRepository reviewRepository,
                       @Value("${hotel.fallback-photos:}") List<String> extraFallbackPhotos) {
        this.reviewRepository = reviewRepository;

        List<String> merged = new ArrayList<>(DEFAULT_FALLBACK_PHOTOS);
        extraFallbackPhotos.stream()
            .map(String::trim)
            .filter(url -> !url.isEmpty())
            .filter(url -> !merged.contains(url))
            .forEach(merged::add);

        this.fallbackPhotos = List.copyOf(merged);
    }

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
            .hotelType(hotel.getHotelType() != null ? hotel.getHotelType().name() : null)
            .mainImageUrl(hotel.getMainImageUrl())
            .isFeatured(hotel.getIsFeatured())
            .amenities(hotel.getAmenities().stream().map(Enum::name).collect(Collectors.toSet()))
            .imageUrls(buildGallery(hotel))
            .averageRating(Math.round(avgRating * 10.0) / 10.0)
            .reviewCount(reviewCount)
            .build();
    }

    /**
     * Returns the hotel's own gallery, topped up with fallback facility photos when
     * it has fewer than {@link #MIN_GALLERY_IMAGES}. The fill is deterministic per
     * hotel (seeded by id), so a given hotel's gallery is stable across requests but
     * varied across hotels. The hotel's own images and {@code mainImageUrl} are never
     * duplicated by a fallback.
     */
    private List<String> buildGallery(Hotel hotel) {
        List<String> gallery = new ArrayList<>(
            hotel.getImages().stream().map(HotelImage::getImageUrl).toList());
        if (gallery.size() >= MIN_GALLERY_IMAGES) {
            return gallery;
        }

        List<String> pool = new ArrayList<>(this.fallbackPhotos);
        pool.removeAll(gallery);
        pool.remove(hotel.getMainImageUrl());
        Collections.shuffle(pool, new Random(hotel.getId() != null ? hotel.getId() : 0L));

        for (String url : pool) {
            if (gallery.size() >= MIN_GALLERY_IMAGES) break;
            gallery.add(url);
        }
        return gallery;
    }
}