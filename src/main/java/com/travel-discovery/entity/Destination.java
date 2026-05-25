package com.traveldiscovery.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "destinations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Destination extends BaseEntity {

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "tagline")
    private String tagline;

    @Column(name = "hero_image_url")
    private String heroImageUrl;

    @Column(name = "avg_price_per_night", precision = 10, scale = 2)
    private BigDecimal avgPricePerNight;

    @Column(name = "is_trending")
    @Builder.Default
    private Boolean isTrending = false;
}
