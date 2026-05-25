package com.traveldiscovery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hotels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String externalId;

    private String name;
    private String city;
    private String country;
    private Integer stars;
    private Double pricePerNight;
    private Double rating;
    private Integer reviewsCount;
    private String imageUrl;

    @Column(length = 1000)
    private String amenities;

    @Column(length = 2000)
    private String description;

    private Double latitude;
    private Double longitude;
}
