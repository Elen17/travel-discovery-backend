package com.travel.discovery.service;

import com.travel.discovery.entity.Hotel;
import com.travel.discovery.entity.HotelImage;
import com.travel.discovery.entity.enums.AmenityType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Talks to the RapidAPI Booking.com endpoint and maps responses straight into
 * transient {@link Hotel} entities. Persistence, quota and de-duplication are
 * the {@link HotelWrapperService}'s job — this class only fetches and maps.
 */
@Service
@Slf4j
public class RapidApiHotelService {

    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${rapidapi.host:booking-com15.p.rapidapi.com}")
    private String apiHost;

    private static final String BASE_URL = "https://booking-com15.p.rapidapi.com/api/v1";
    private final RestTemplate restTemplate = new RestTemplate();

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", apiHost);
        return headers;
    }

    // Step 1: resolve city name → dest_id
    public Map<String, Object> searchDestination(String cityName) {
        String url = BASE_URL + "/hotels/searchDestination?query=" + cityName;
        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers()), Map.class
        );
        return response.getBody();
    }

    // Step 2: search hotels using dest_id, mapped to transient Hotel entities
    @SuppressWarnings("unchecked")
    public List<Hotel> searchHotels(String city, String checkIn,
                                    String checkOut, int adults) {
        try {
            // Resolve destination first
            Map<String, Object> destResult = searchDestination(city);
            List<Map<String, Object>> destData = (List<Map<String, Object>>) destResult.get("data");
            if (destData == null || destData.isEmpty()) return Collections.emptyList();

            Map<String, Object> dest = destData.get(0);
            String destId = String.valueOf(dest.get("dest_id"));
            String searchType = String.valueOf(dest.get("dest_type"));
            // The destination row carries the country name — far more reliable than
            // anything on the individual hotel rows.
            String country = dest.get("country") != null ? String.valueOf(dest.get("country")) : "";

            String url = BASE_URL + "/hotels/searchHotels"
                    + "?dest_id=" + destId
                    + "&search_type=" + searchType
                    + "&arrival_date=" + checkIn
                    + "&departure_date=" + checkOut
                    + "&adults=" + adults
                    + "&currency_code=USD&languagecode=en-us";

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers()), Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body == null) return Collections.emptyList();

            Map<String, Object> data = (Map<String, Object>) body.get("data");
            if (data == null) return Collections.emptyList();

            List<Map<String, Object>> hotels = (List<Map<String, Object>>) data.get("hotels");
            if (hotels == null) return Collections.emptyList();

            log.info("Fetched {} hotels from RapidAPI for city '{}'", hotels.size(), city);
            log.debug("RapidAPI raw hotels payload: {}", hotels);
            return hotels.stream().map(h -> mapToHotel(h, city, country)).toList();

        } catch (Exception e) {
            throw new RuntimeException("RapidAPI call failed: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private Hotel mapToHotel(Map<String, Object> raw, String city, String country) {
        log.debug("Mapping RapidAPI hotel: {}", raw);

        String description = (String) raw.getOrDefault("accessibilityLabel", "");
        Map<String, Object> property = (Map<String, Object>) raw.getOrDefault("property", raw);
        Map<String, Object> priceBreakdown = (Map<String, Object>) property.get("priceBreakdown");
        Map<String, Object> grossPrice = priceBreakdown != null
                ? (Map<String, Object>) priceBreakdown.get("grossPrice")
                : null;

        List<String> photoUrls = extractPhotoUrls(property.get("photoUrls"));

        BigDecimal price = grossPrice != null && grossPrice.get("value") != null
                ? BigDecimal.valueOf(((Number) grossPrice.get("value")).doubleValue())
                : BigDecimal.ZERO;

        Hotel hotel = Hotel.builder()
                .externalId(String.valueOf(property.getOrDefault("id", "")))
                .name(String.valueOf(property.getOrDefault("name", "Unknown Hotel")))
                .city(city)
                .country(country)
                .description(description)
                .starRating(((Number) property.getOrDefault("propertyClass", 3)).shortValue())
                .pricePerNight(price)
                .mainImageUrl(photoUrls.isEmpty() ? null : photoUrls.get(0))
                .latitude(toBigDecimal(property.get("latitude")))
                .longitude(toBigDecimal(property.get("longitude")))
                // The search endpoint doesn't return an amenity list, so generate a
                // plausible random set (always including WiFi) for demo purposes.
                .amenities(randomAmenities())
                .build();

        int sortOrder = 0;
        for (String url : photoUrls) {
            hotel.getImages().add(HotelImage.builder()
                    .hotel(hotel)
                    .imageUrl(url)
                    .sortOrder(sortOrder++)
                    .build());
        }
        return hotel;
    }

    private BigDecimal toBigDecimal(Object value) {
        return value instanceof Number n ? BigDecimal.valueOf(n.doubleValue()) : null;
    }

    /**
     * Builds a random, non-empty amenity set (WiFi always included) since the search
     * endpoint doesn't provide facilities. Picks a random number of distinct amenities.
     */
    private Set<AmenityType> randomAmenities() {
        List<AmenityType> pool = new ArrayList<>(Arrays.asList(AmenityType.values()));
        Collections.shuffle(pool);
        int count = ThreadLocalRandom.current().nextInt(2, pool.size() + 1);
        Set<AmenityType> amenities = EnumSet.copyOf(pool.subList(0, count));
        amenities.add(AmenityType.WIFI);
        return amenities;
    }

    /**
     * Normalises the API's "photoUrls" value into a clean list of URLs.
     * Handles a real JSON array as well as a value that arrives already
     * flattened to a "[url1, url2, ...]" string.
     */
    private List<String> extractPhotoUrls(Object value) {
        if (value instanceof List<?> list) {
            return list.stream().map(String::valueOf).filter(s -> !s.isBlank()).toList();
        }
        if (value instanceof String s && !s.isBlank()) {
            String trimmed = s.trim();
            if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                trimmed = trimmed.substring(1, trimmed.length() - 1);
            }
            return Arrays.stream(trimmed.split(","))
                    .map(String::trim)
                    .filter(url -> !url.isBlank())
                    .toList();
        }
        return List.of();
    }
}