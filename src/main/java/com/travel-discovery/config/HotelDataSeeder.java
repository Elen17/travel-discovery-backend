package com.traveldiscovery.config;

import com.traveldiscovery.entity.Hotel;
import com.traveldiscovery.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

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

        List<Hotel> hotels = List.of(
            hotel("seed-001", "Hotel Ritz Paris", "Paris", "France", 5,
                320.0, 9.2, 2841,
                "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?w=600",
                "WiFi,Pool,Spa,Restaurant,Gym,Concierge",
                "Iconic luxury hotel in the heart of Paris, steps from Place Vendôme.",
                48.8698, 2.3278),

            hotel("seed-002", "Majestic Hotel Barcelona", "Barcelona", "Spain", 4,
                180.0, 8.7, 1423,
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=600",
                "WiFi,Beach Access,Pool,Bar,Restaurant",
                "Classic hotel on Passeig de Gràcia with stunning city views.",
                41.3879, 2.1699),

            hotel("seed-003", "Alpine Lodge Zurich", "Zurich", "Switzerland", 3,
                95.0, 8.1, 612,
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                "WiFi,Breakfast,Parking,Sauna",
                "Cozy mountain-inspired lodge with easy access to the city centre.",
                47.3769, 8.5417),

            hotel("seed-004", "The Shard Hotel London", "London", "UK", 5,
                450.0, 9.5, 3214,
                "https://images.unsplash.com/photo-1548574505-5e239809ee19?w=600",
                "WiFi,Spa,Restaurant,Bar,Gym,Room Service",
                "Breathtaking views of London from one of Europe's tallest buildings.",
                51.5045, -0.0865),

            hotel("seed-005", "Hotel Colosseum Rome", "Rome", "Italy", 4,
                220.0, 8.9, 1876,
                "https://images.unsplash.com/photo-1552832230-c0197dd311b5?w=600",
                "WiFi,Restaurant,Bar,Tour Desk,Rooftop",
                "Historic hotel with views of the Colosseum and authentic Italian dining.",
                41.8902, 12.4922),

            hotel("seed-006", "Santorini Cliffside Resort", "Santorini", "Greece", 5,
                380.0, 9.6, 2102,
                "https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?w=600",
                "WiFi,Infinity Pool,Spa,Restaurant,Bar,Private Beach",
                "Iconic white-washed cliffside resort with stunning Aegean Sea views.",
                36.3932, 25.4615),

            hotel("seed-007", "Grand Hotel Amsterdam", "Amsterdam", "Netherlands", 4,
                195.0, 8.5, 987,
                "https://images.unsplash.com/photo-1534351590666-13e3e96b5702?w=600",
                "WiFi,Canal View,Bar,Breakfast,Bike Rental",
                "Charming canal-side hotel in a beautifully restored 17th-century building.",
                52.3676, 4.9041),

            hotel("seed-008", "Prague Castle View Hotel", "Prague", "Czech Republic", 3,
                85.0, 7.9, 543,
                "https://images.unsplash.com/photo-1519677100203-a0e668c92439?w=600",
                "WiFi,Breakfast,Bar,City Tours",
                "Affordable comfort hotel with direct views of Prague Castle.",
                50.0880, 14.4208)
        );

        hotelRepository.saveAll(hotels);
        log.info("Seeded {} hotels into local database.", hotels.size());
    }

    private Hotel hotel(String extId, String name, String city, String country,
                        int stars, double price, double rating, int reviews,
                        String image, String amenities, String description,
                        double lat, double lon) {
        Hotel h = new Hotel();
        h.setExternalId(extId);
        h.setName(name);
        h.setCity(city);
        h.setCountry(country);
        h.setStars(stars);
        h.setPricePerNight(price);
        h.setRating(rating);
        h.setReviewsCount(reviews);
        h.setImageUrl(image);
        h.setAmenities(amenities);
        h.setDescription(description);
        h.setLatitude(lat);
        h.setLongitude(lon);
        return h;
    }
}
