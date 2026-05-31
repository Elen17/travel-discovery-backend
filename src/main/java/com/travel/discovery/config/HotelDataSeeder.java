package com.travel.discovery.config;

import com.travel.discovery.entity.Hotel;
import com.travel.discovery.entity.enums.AmenityType;
import com.travel.discovery.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@Order(1)
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

        List<Hotel> hotels = List.of(
            hotel("seed-001", "Hotel Ritz Paris", "Paris", "France", 5,
                "320.00", 9.2,
                "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?w=600",
                "Iconic luxury hotel in the heart of Paris, steps from Place Vendôme.",
                "48.8698", "2.3278",
                Set.of(AmenityType.WIFI, AmenityType.POOL, AmenityType.SPA, AmenityType.RESTAURANT, AmenityType.GYM)),

            hotel("seed-002", "Majestic Hotel Barcelona", "Barcelona", "Spain", 4,
                "180.00", 8.7,
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=600",
                "Classic hotel on Passeig de Gràcia with stunning city views.",
                "41.3879", "2.1699",
                Set.of(AmenityType.WIFI, AmenityType.POOL, AmenityType.RESTAURANT)),

            hotel("seed-003", "Alpine Lodge Zurich", "Zurich", "Switzerland", 3,
                "95.00", 8.1,
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                "Cozy mountain-inspired lodge with easy access to the city centre.",
                "47.3769", "8.5417",
                Set.of(AmenityType.WIFI, AmenityType.PARKING)),

            hotel("seed-004", "The Shard Hotel London", "London", "United Kingdom", 5,
                "450.00", 9.5,
                "https://images.unsplash.com/photo-1548574505-5e239809ee19?w=600",
                "Breathtaking views of London from one of Europe's tallest buildings.",
                "51.5045", "-0.0865",
                Set.of(AmenityType.WIFI, AmenityType.SPA, AmenityType.RESTAURANT, AmenityType.GYM)),

            hotel("seed-005", "Hotel Colosseum Rome", "Rome", "Italy", 4,
                "220.00", 8.9,
                "https://images.unsplash.com/photo-1552832230-c0197dd311b5?w=600",
                "Historic hotel with views of the Colosseum and authentic Italian dining.",
                "41.8902", "12.4922",
                Set.of(AmenityType.WIFI, AmenityType.RESTAURANT)),

            hotel("seed-006", "Santorini Cliffside Resort", "Santorini", "Greece", 5,
                "380.00", 9.6,
                "https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?w=600",
                "Iconic white-washed cliffside resort with stunning Aegean Sea views.",
                "36.3932", "25.4615",
                Set.of(AmenityType.WIFI, AmenityType.POOL, AmenityType.SPA, AmenityType.RESTAURANT)),

            hotel("seed-007", "Grand Hotel Amsterdam", "Amsterdam", "Netherlands", 4,
                "195.00", 8.5,
                "https://images.unsplash.com/photo-1534351590666-13e3e96b5702?w=600",
                "Charming canal-side hotel in a beautifully restored 17th-century building.",
                "52.3676", "4.9041",
                Set.of(AmenityType.WIFI, AmenityType.RESTAURANT)),

            hotel("seed-008", "Prague Castle View Hotel", "Prague", "Czechia (Czech Republic)", 3,
                "85.00", 7.9,
                "https://images.unsplash.com/photo-1519677100203-a0e668c92439?w=600",
                "Affordable comfort hotel with direct views of Prague Castle.",
                "50.0880", "14.4208",
                Set.of(AmenityType.WIFI))
        );

        hotelRepository.saveAll(hotels);
        log.info("Seeded {} hotels.", hotels.size());
    }

    private Hotel hotel(String extId, String name, String city, String country,
                        int stars, String price, double rating,
                        String image, String description,
                        String lat, String lon, Set<AmenityType> amenities) {
        return Hotel.builder()
            .externalId(extId)
            .name(name)
            .city(city)
            .country(country)
            .starRating((short) stars)
            .pricePerNight(new BigDecimal(price))
            .mainImageUrl(image)
            .description(description)
            .latitude(new BigDecimal(lat))
            .longitude(new BigDecimal(lon))
            .amenities(amenities)
            .isFeatured(stars >= 5)
            .build();
    }
}
