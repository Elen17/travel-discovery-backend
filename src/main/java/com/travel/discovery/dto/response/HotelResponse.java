package com.travel.discovery.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class HotelResponse {
    private Long id;
    private String name;
    private String description;
    private String country;
    private String city;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal pricePerNight;
    private Short starRating;
    private String mainImageUrl;
    private Boolean isFeatured;
    private Set<String> amenities;
    private List<String> imageUrls;
    private Double averageRating;
    private Long reviewCount;
}
