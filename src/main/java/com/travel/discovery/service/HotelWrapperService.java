package com.travel.discovery.service;

import com.travel.discovery.dto.HotelDTO;
import com.travel.discovery.entity.ApiQuota;
import com.travel.discovery.entity.Hotel;
import com.travel.discovery.entity.enums.AmenityType;
import com.travel.discovery.repository.ApiQuotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelWrapperService {

    private final RapidApiHotelService rapidApiService;
    private final LocalHotelService localHotelService;
    private final ApiQuotaRepository quotaRepository;

    private static final String PROVIDER = "rapidapi";

    @Value("${rapidapi.quota.limit:500}")
    private int quotaLimit;

    public List<HotelDTO> searchHotels(String city, String checkIn, String checkOut, int adults) {
        if (isQuotaActive()) {
            try {
                log.info("Fetching hotels from RapidAPI for city: {}", city);
                List<HotelDTO> hotels = rapidApiService.searchHotels(city, checkIn, checkOut, adults);
                syncToLocalDb(hotels, city);
                incrementQuota();
                return hotels;
            } catch (Exception e) {
                log.warn("RapidAPI call failed ({}), falling back to local DB", e.getMessage());
            }
        }
        log.info("Serving from local DB for city: {}", city);
        return localHotelService.searchHotels(city);
    }

    public List<HotelDTO> getAllHotels() {
        return localHotelService.getAllHotels();
    }

    public ApiQuota getQuotaStatus() {
        return quotaRepository.findById(PROVIDER).orElse(new ApiQuota(PROVIDER, quotaLimit));
    }

    private boolean isQuotaActive() {
        ApiQuota quota = quotaRepository.findById(PROVIDER)
            .orElseGet(() -> quotaRepository.save(new ApiQuota(PROVIDER, quotaLimit)));
        if (quota.getResetDate() != null && LocalDate.now().isAfter(quota.getResetDate())) {
            quota.setCallsUsed(0);
            quota.setResetDate(LocalDate.now().withDayOfMonth(1).plusMonths(1));
            quotaRepository.save(quota);
        }
        return quota.isActive();
    }

    private void incrementQuota() {
        quotaRepository.findById(PROVIDER).ifPresent(q -> {
            q.setCallsUsed(q.getCallsUsed() + 1);
            quotaRepository.save(q);
        });
    }

    private void syncToLocalDb(List<HotelDTO> hotels, String city) {
        for (HotelDTO dto : hotels) {
            if (dto.getHotelId() == null) continue;
            if (localHotelService.existsByExternalId(dto.getHotelId())) continue;

            // FIX: Build Hotel using new entity fields
            Set<AmenityType> amenities = new HashSet<>();
            if (dto.getAmenities() != null) {
                for (String a : dto.getAmenities()) {
                    try { amenities.add(AmenityType.valueOf(a.toUpperCase())); }
                    catch (IllegalArgumentException ignored) { /* skip unknown amenity strings from API */ }
                }
            }

            Hotel entity = Hotel.builder()
                .externalId(dto.getHotelId())
                .name(dto.getName())
                .city(city)
                .country(dto.getCountry() != null ? dto.getCountry() : "")
                .starRating(dto.getStars() != null ? dto.getStars() : 3)
                .pricePerNight(dto.getPricePerNight() != null
                    ? BigDecimal.valueOf(dto.getPricePerNight()) : BigDecimal.ZERO)
                .mainImageUrl(dto.getImageUrl())
                .description(dto.getDescription())
                .latitude(dto.getLatitude() != null ? BigDecimal.valueOf(dto.getLatitude()) : null)
                .longitude(dto.getLongitude() != null ? BigDecimal.valueOf(dto.getLongitude()) : null)
                .amenities(amenities)
                .build();

            localHotelService.saveHotel(entity);
        }
    }
}
