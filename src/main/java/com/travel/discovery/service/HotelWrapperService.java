package com.travel.discovery.service;

import com.travel.discovery.dto.response.HotelResponse;
import com.travel.discovery.entity.ApiQuota;
import com.travel.discovery.entity.Hotel;
import com.travel.discovery.mapper.HotelMapper;
import com.travel.discovery.repository.ApiQuotaRepository;
import com.travel.discovery.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Search entry point that keeps the local DB as the source of truth.
 *
 * <p>RapidAPI is treated as an ingestion source: when quota allows, results are
 * fetched and upserted into the {@code hotels} table; then the response is built
 * <em>from the DB</em> so search results carry the bookable Long id and the same
 * {@link HotelResponse} shape as the rest of the app. If the API is unavailable
 * or quota is exhausted, we simply serve whatever the DB already holds.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HotelWrapperService {

    private final RapidApiHotelService rapidApiService;
    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;
    private final ApiQuotaRepository quotaRepository;
    private final LocationService locationService;

    private static final String PROVIDER = "rapidapi";
    private static final int MAX_SEARCH_RESULTS = 50;

    @Value("${rapidapi.quota.limit:500}")
    private int quotaLimit;

    /**
     * Hotels re-synced more recently than this are left untouched (avoids wasting quota).
     */
    @Value("${rapidapi.sync.ttl-hours:24}")
    private long syncTtlHours;

    @Transactional
    public List<HotelResponse> searchHotels(String country, String city, String checkIn, String checkOut, int adults) {
        // Reject unknown country/city before doing any work (API call or DB query).
        locationService.validateLocation(country, city);

        if (isQuotaActive()) {
            try {
                log.info("Fetching hotels from RapidAPI for city: {}", city);
                List<Hotel> hotels = rapidApiService.searchHotels(city, checkIn, checkOut, adults);
                syncToLocalDb(hotels);
                incrementQuota();
            } catch (Exception e) {
                log.warn("RapidAPI call failed ({}), serving local DB only", e.getMessage());
            }
        } else {
            log.info("RapidAPI quota exhausted; serving local DB for city: {}", city);
        }

        return hotelRepository
                .searchHotels(country, city, null, PageRequest.of(0, MAX_SEARCH_RESULTS))
                .getContent().stream()
                .map(hotelMapper::toResponse)
                .toList();
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

    /**
     * Inserts new hotels and refreshes stale ones. Existing hotels still within the
     * TTL window are skipped. On refresh only the volatile scalar fields (price,
     * description, rating, coordinates, main image) are updated; image rows and
     * user reviews are left intact.
     */
    private void syncToLocalDb(List<Hotel> hotels) {
        for (Hotel incoming : hotels) {
            if (incoming.getExternalId() == null || incoming.getExternalId().isBlank()) continue;

            Optional<Hotel> existingOpt = hotelRepository.findByExternalId(incoming.getExternalId());
            if (existingOpt.isEmpty()) {
                hotelRepository.save(incoming);
                continue;
            }

            Hotel existing = existingOpt.get();
            if (isFresh(existing)) continue;

            existing.setName(incoming.getName());
            existing.setCountry(incoming.getCountry());
            existing.setDescription(incoming.getDescription());
            existing.setStarRating(incoming.getStarRating());
            existing.setPricePerNight(incoming.getPricePerNight());
            existing.setLatitude(incoming.getLatitude());
            existing.setLongitude(incoming.getLongitude());
            if (incoming.getMainImageUrl() != null) {
                existing.setMainImageUrl(incoming.getMainImageUrl());
            }
            hotelRepository.save(existing);
        }
    }

    private boolean isFresh(Hotel hotel) {
        return hotel.getUpdatedAt() != null
                && hotel.getUpdatedAt().isAfter(Instant.now().minus(syncTtlHours, ChronoUnit.HOURS));
    }
}