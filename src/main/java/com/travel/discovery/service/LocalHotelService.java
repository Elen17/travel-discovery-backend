package com.travel.discovery.service;

import com.travel.discovery.dto.HotelDTO;
import com.travel.discovery.entity.Hotel;
import com.travel.discovery.entity.enums.AmenityType;
import com.travel.discovery.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocalHotelService {

    private final HotelRepository hotelRepository;

    public List<HotelDTO> searchHotels(String city) {
        // FIX: use searchHotels JPQL query instead of findByCityIgnoreCase
        return hotelRepository.searchHotels(null, city, null, null, null, PageRequest.of(0, 50))
            .getContent().stream()
            .map(this::mapToDTO)
            .toList();
    }

    public List<HotelDTO> getAllHotels() {
        return hotelRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public void saveHotel(Hotel hotel) {
        hotelRepository.save(hotel);
    }

    public boolean existsByExternalId(String externalId) {
        return hotelRepository.existsByExternalId(externalId);
    }

    private HotelDTO mapToDTO(Hotel hotel) {
        // FIX: map from new entity fields (starRating, mainImageUrl, pricePerNight as BigDecimal, amenities as Set<AmenityType>)
        List<String> amenityNames = hotel.getAmenities() == null
            ? List.of()
            : hotel.getAmenities().stream().map(AmenityType::name).toList();

        return HotelDTO.builder()
            .hotelId(hotel.getExternalId())
            .name(hotel.getName())
            .city(hotel.getCity())
            .country(hotel.getCountry())
            .stars(hotel.getStarRating())
            .pricePerNight(hotel.getPricePerNight() != null ? hotel.getPricePerNight().doubleValue() : 0.0)
            .rating(0.0)
            .reviewsCount(0)
            .imageUrl(hotel.getMainImageUrl())
            .amenities(amenityNames)
            .description(hotel.getDescription())
            .latitude(hotel.getLatitude() != null ? hotel.getLatitude().doubleValue() : null)
            .longitude(hotel.getLongitude() != null ? hotel.getLongitude().doubleValue() : null)
            .dataSource("local_db")
            .build();
    }
}
