package com.traveldiscovery.config;

import com.traveldiscovery.entity.Hotel;
import com.traveldiscovery.entity.enums.AmenityType;
import com.traveldiscovery.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotelDataSeeder implements CommandLineRunner {

    private final HotelRepository hotelRepository;

    @Override
    public void run(String... args) {
        if (hotelRepository.count() > 0) {
            log.info("Hotel data already seeded, skipping.");
            return;
        }
        log.info("Seeding initial hotel data...");
        hotelRepository.saveAll(List.of(
            hotel("seed-001", "Hotel Ritz Paris", "Paris", "France", 5,
                "320.00", "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?w=600",
                Set.of(AmenityType.WIFI, AmenityType.POOL, AmenityType.SPA, AmenityType.RESTAURANT, AmenityType.GYM),
                "Iconic luxury hotel steps from Place Vendôme.", true),
            hotel("seed-002", "Majestic Hotel Barcelona", "Barcelona", "Spain", 4,
                "180.00", "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=600",
                Set.of(AmenityType.WIFI, AmenityType.POOL, AmenityType.RESTAURANT),
                "Classic hotel on Passeig de Gràcia with stunning city views.", false),
            hotel("seed-003", "Alpine Lodge Zurich", "Zurich", "Switzerland", 3,
                "95.00", "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                Set.of(AmenityType.WIFI, AmenityType.PARKING),
                "Cozy mountain-inspired lodge with easy city centre access.", false),
            hotel("seed-004", "The Shard Hotel London", "London", "UK", 5,
                "450.00", "https://images.unsplash.com/photo-1548574505-5e239809ee19?w=600",
                Set.of(AmenityType.WIFI, AmenityType.SPA, AmenityType.RESTAURANT, AmenityType.GYM),
                "Breathtaking views of London from one of Europe's tallest buildings.", true),
            hotel("seed-005", "Hotel Colosseum Rome", "Rome", "Italy", 4,
                "220.00", "https://images.unsplash.com/photo-1552832230-c0197dd311b5?w=600",
                Set.of(AmenityType.WIFI, AmenityType.RESTAURANT),
                "Historic hotel with views of the Colosseum.", false),
            hotel("seed-006", "Santorini Cliffside Resort", "Santorini", "Greece", 5,
                "380.00", "https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?w=600",
                Set.of(AmenityType.WIFI, AmenityType.POOL, AmenityType.SPA, AmenityType.RESTAURANT),
                "Iconic white-washed cliffside resort with Aegean Sea views.", true),
            hotel("seed-007", "Grand Hotel Amsterdam", "Amsterdam", "Netherlands", 4,
                "195.00", "https://images.unsplash.com/photo-1534351590666-13e3e96b5702?w=600",
                Set.of(AmenityType.WIFI, AmenityType.RESTAURANT),
                "Charming canal-side hotel in a restored 17th-century building.", false),
            hotel("seed-008", "Prague Castle View Hotel", "Prague", "Czech Republic", 3,
                "85.00", "https://images.unsplash.com/photo-1519677100203-a0e668c92439?w=600",
                Set.of(AmenityType.WIFI, AmenityType.PARKING),
                "Affordable comfort hotel with direct views of Prague Castle.", false)
        ));
        log.info("Seeded 8 hotels into local database.");
    }

    private Hotel hotel(String extId, String name, String city, String country,
                        int stars, String price, String image,
                        Set<AmenityType> amenities, String description, boolean featured) {
        return Hotel.builder()
            .externalId(extId)
            .name(name)
            .city(city)
            .country(country)
            .starRating(stars)
            .pricePerNight(new BigDecimal(price))
            .mainImageUrl(image)
            .amenities(amenities)
            .description(description)
            .isFeatured(featured)
            .build();
    }
}
