package com.traveldiscovery.service;

import com.traveldiscovery.dto.HotelDTO;
import com.traveldiscovery.entity.ApiQuota;
import com.traveldiscovery.entity.Hotel;
import com.traveldiscovery.repository.ApiQuotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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

    public List<HotelDTO> searchHotels(String city, String checkIn,
                                        String checkOut, int adults) {
        if (isQuotaActive()) {
            try {
                log.info("Fetching hotels from RapidAPI for city: {}", city);
                List<HotelDTO> hotels = rapidApiService.searchHotels(city, checkIn, checkOut, adults);

                // Sync results into local DB for future fallback
                syncToLocalDb(hotels, city);

                // Increment quota counter
                incrementQuota();

                log.info("RapidAPI returned {} hotels, synced to DB", hotels.size());
                return hotels;

            } catch (Exception e) {
                log.warn("RapidAPI call failed ({}), falling back to local DB", e.getMessage());
                return localHotelService.searchHotels(city);
            }
        }

        log.info("RapidAPI quota exhausted — serving from local DB for city: {}", city);
        return localHotelService.searchHotels(city);
    }

    public List<HotelDTO> getAllHotels() {
        return localHotelService.getAllHotels();
    }

    // ─── Quota management ───────────────────────────────────────────────────

    private boolean isQuotaActive() {
        ApiQuota quota = quotaRepository.findById(PROVIDER)
            .orElseGet(() -> {
                ApiQuota newQuota = new ApiQuota(PROVIDER, quotaLimit);
                return quotaRepository.save(newQuota);
            });

        // Reset counter if new month has started
        if (quota.getResetDate() != null && LocalDate.now().isAfter(quota.getResetDate())) {
            quota.setCallsUsed(0);
            quota.setResetDate(LocalDate.now().withDayOfMonth(1).plusMonths(1));
            quotaRepository.save(quota);
            log.info("RapidAPI quota reset for new month");
        }

        return quota.isActive();
    }

    private void incrementQuota() {
        quotaRepository.findById(PROVIDER).ifPresent(quota -> {
            quota.setCallsUsed(quota.getCallsUsed() + 1);
            quotaRepository.save(quota);
            log.debug("RapidAPI calls used: {}/{}", quota.getCallsUsed(), quota.getCallsLimit());
        });
    }

    // ─── DB sync ────────────────────────────────────────────────────────────

    private void syncToLocalDb(List<HotelDTO> hotels, String city) {
        for (HotelDTO dto : hotels) {
            if (dto.getHotelId() == null) continue;
            if (localHotelService.existsByExternalId(dto.getHotelId())) continue;

            Hotel entity = new Hotel();
            entity.setExternalId(dto.getHotelId());
            entity.setName(dto.getName());
            entity.setCity(city);
            entity.setCountry(dto.getCountry() != null ? dto.getCountry() : "");
            entity.setStars(dto.getStars() != null ? dto.getStars() : 3);
            entity.setPricePerNight(dto.getPricePerNight() != null ? dto.getPricePerNight() : 0.0);
            entity.setRating(dto.getRating() != null ? dto.getRating() : 0.0);
            entity.setReviewsCount(dto.getReviewsCount() != null ? dto.getReviewsCount() : 0);
            entity.setImageUrl(dto.getImageUrl());
            entity.setAmenities(dto.getAmenities() != null
                ? String.join(",", dto.getAmenities())
                : "");
            entity.setDescription(dto.getDescription());
            entity.setLatitude(dto.getLatitude());
            entity.setLongitude(dto.getLongitude());

            localHotelService.saveHotel(entity);
        }
    }

    // ─── Quota status endpoint ───────────────────────────────────────────────

    public ApiQuota getQuotaStatus() {
        return quotaRepository.findById(PROVIDER)
            .orElse(new ApiQuota(PROVIDER, quotaLimit));
    }
}
