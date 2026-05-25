package com.traveldiscovery.service;

import com.traveldiscovery.dto.HotelDTO;
import com.traveldiscovery.entity.Hotel;
import com.traveldiscovery.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocalHotelService {

    private final HotelRepository hotelRepository;

    public List<HotelDTO> searchHotels(String city) {
        return hotelRepository.findByCityIgnoreCase(city)
            .stream()
            .map(this::mapToDTO)
            .toList();
    }

    public List<HotelDTO> getAllHotels() {
        return hotelRepository.findAll()
            .stream()
            .map(this::mapToDTO)
            .toList();
    }

    public void saveHotel(Hotel hotel) {
        hotelRepository.save(hotel);
    }

    public boolean existsByExternalId(String externalId) {
        return hotelRepository.existsByExternalId(externalId);
    }

    private HotelDTO mapToDTO(Hotel hotel) {
        List<String> amenitiesList = hotel.getAmenities() != null
            ? Arrays.asList(hotel.getAmenities().split(","))
            : List.of();

        return HotelDTO.builder()
            .hotelId(hotel.getExternalId())
            .name(hotel.getName())
            .city(hotel.getCity())
            .country(hotel.getCountry())
            .stars(hotel.getStars())
            .pricePerNight(hotel.getPricePerNight())
            .rating(hotel.getRating())
            .reviewsCount(hotel.getReviewsCount())
            .imageUrl(hotel.getImageUrl())
            .amenities(amenitiesList)
            .description(hotel.getDescription())
            .latitude(hotel.getLatitude())
            .longitude(hotel.getLongitude())
            .dataSource("local_db")
            .build();
    }
}
