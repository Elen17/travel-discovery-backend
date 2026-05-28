package com.travel.discovery.entity;

import com.travel.discovery.entity.enums.AmenityType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "hotels")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Hotel extends BaseEntity {

    @Column(name = "external_id", unique = true)
    private String externalId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "address")
    private String address;

    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal pricePerNight = BigDecimal.ZERO;

    @Column(name = "star_rating", nullable = false)
    @Builder.Default
    private Short starRating = 3;

    @Column(name = "main_image_url")
    private String mainImageUrl;

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    @ElementCollection(targetClass = AmenityType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "hotel_amenities", joinColumns = @JoinColumn(name = "hotel_id"))
    @Column(name = "amenity_type")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<AmenityType> amenities = new HashSet<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<HotelImage> images = new ArrayList<>();
}
