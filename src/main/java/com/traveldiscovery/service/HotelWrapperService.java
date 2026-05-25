package com.traveldiscovery.service;

import com.traveldiscovery.entity.ApiQuota;
import com.traveldiscovery.entity.Hotel;
import com.traveldiscovery.entity.enums.AmenityType;
import com.traveldiscovery.repository.ApiQuotaRepository;
import com.traveldiscovery.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelWrapperService {

    private final RapidApiHotelService rapidApiService;
    private final HotelRepository hotelRepository;
    private final ApiQuotaRepository quotaRepository;

    private static final String PROVIDER = "rapidapi";

    @Value("${rapidapi.quota.limit:500}")
    private int quotaLimit;

    /**
     * Syncs hotels from RapidAPI into the local DB for a given city.
     * Returns the count of newly inserted hotels.
     * Falls back gracefully if quota is exhausted or API call fails.
     */
    public int syncCity(String city, String checkIn, String checkOut, int adults) {
        if (!isQuotaActive()) {
            log.info("RapidAPI quota exhausted — skipping sync for city: {}", city);
            return 0;
        }
        try {
            List<Map<String, Object>> rawHotels =
                rapidApiService.fetchRawHotels(city, checkIn, checkOut, adults);

            // Batch check which external IDs already exist
            List<String> externalIds = rawHotels.stream()
                .map(h -> String.valueOf(extractProperty(h, "id")))
                .collect(Collectors.toList());

            Set<String> existing = new HashSet<>(
                hotelRepository.findExternalIdsByExternalIdIn(externalIds)
            );

            List<Hotel> toSave = rawHotels.stream()
                .map(raw -> mapToEntity(raw, city))
                .filter(h -> h.getExternalId() != null
                    && !existing.contains(h.getExternalId()))
                .collect(Collectors.toList());

            if (!toSave.isEmpty()) {
                hotelRepository.saveAll(toSave);
                log.info("Synced {} new hotels for city: {}", toSave.size(), city);
            }

            incrementQuota();
            return toSave.size();

        } catch (Exception e) {
            log.warn("RapidAPI sync failed for city {}: {}", city, e.getMessage());
            return 0;
        }
    }

    public ApiQuota getQuotaStatus() {
        return quotaRepository.findById(PROVIDER)
            .orElse(new ApiQuota(PROVIDER, quotaLimit));
    }

    // ─── Quota management ────────────────────────────────────────────────────

    private boolean isQuotaActive() {
        ApiQuota quota = quotaRepository.findById(PROVIDER)
            .orElseGet(() -> quotaRepository.save(new ApiQuota(PROVIDER, quotaLimit)));

        if (quota.getResetDate() != null && LocalDate.now().isAfter(quota.getResetDate())) {
            quota.setCallsUsed(0);
            quota.setResetDate(LocalDate.now().withDayOfMonth(1).plusMonths(1));
            quotaRepository.save(quota);
            log.info("RapidAPI quota reset for new month");
        }
        return quota.isActive();
    }

    private void incrementQuota() {
        quotaRepository.findById(PROVIDER).ifPresent(q -> {
            q.setCallsUsed(q.getCallsUsed() + 1);
            quotaRepository.save(q);
            log.debug("RapidAPI calls used: {}/{}", q.getCallsUsed(), q.getCallsLimit());
        });
    }

    // ─── Mapping ─────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private Hotel mapToEntity(Map<String, Object> raw, String city) {
        Object propObj = raw.get("property");
        Map<String, Object> property = (propObj instanceof Map) ? (Map<String, Object>) propObj : raw;

        Object priceObj = raw.get("priceBreakdown");
        Map<String, Object> priceBreakdown = (priceObj instanceof Map) ? (Map<String, Object>) priceObj : null;
        Map<String, Object> grossPrice = priceBreakdown != null
            ? (Map<String, Object>) priceBreakdown.get("grossPrice") : null;

        double price = grossPrice != null
            ? ((Number) grossPrice.getOrDefault("value", 0)).doubleValue() : 0.0;

        String externalId = String.valueOf(extractProperty(property, "id"));
        String name = String.valueOf(extractProperty(property, "name"));
        int stars = ((Number) property.getOrDefault("propertyClass", 3)).intValue();

        Object photoUrls = property.get("photoUrls");
        String mainImage = "";
        if (photoUrls instanceof List && !((List<?>) photoUrls).isEmpty()) {
            mainImage = String.valueOf(((List<?>) photoUrls).get(0));
        }

        return Hotel.builder()
            .externalId(externalId)
            .name(name.isBlank() ? "Unknown Hotel" : name)
            .city(city)
            .country("")
            .starRating(Math.max(1, Math.min(5, stars)))
            .pricePerNight(BigDecimal.valueOf(price))
            .mainImageUrl(mainImage)
            .isFeatured(false)
            .amenities(Set.of(AmenityType.WIFI))
            .build();
    }

    private Object extractProperty(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val : "";
    }
}
