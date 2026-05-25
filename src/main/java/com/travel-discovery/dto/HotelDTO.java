package com.traveldiscovery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelDTO {

    private String hotelId;
    private String name;
    private String city;
    private String country;
    private Integer stars;
    private Double pricePerNight;
    private Double rating;
    private Integer reviewsCount;
    private String imageUrl;
    private List<String> amenities;
    private String description;
    private Double latitude;
    private Double longitude;

    // Indicates where the data came from — useful for debugging
    private String dataSource; // "rapidapi" or "local_db"
}
