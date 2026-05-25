package com.traveldiscovery.service;

import com.traveldiscovery.dto.HotelDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
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

    // Step 2: search hotels using dest_id
    @SuppressWarnings("unchecked")
    public List<HotelDTO> searchHotels(String city, String checkIn,
                                        String checkOut, int adults) {
        try {
            // Resolve destination first
            Map<String, Object> destResult = searchDestination(city);
            List<Map<String, Object>> destData = (List<Map<String, Object>>) destResult.get("data");
            if (destData == null || destData.isEmpty()) return Collections.emptyList();

            String destId = String.valueOf(destData.get(0).get("dest_id"));
            String searchType = String.valueOf(destData.get(0).get("dest_type"));

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

            return hotels.stream().map(h -> mapRapidApiHotel(h, city)).toList();

        } catch (Exception e) {
            throw new RuntimeException("RapidAPI call failed: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private HotelDTO mapRapidApiHotel(Map<String, Object> raw, String city) {
        Map<String, Object> property = (Map<String, Object>) raw.getOrDefault("property", raw);
        Map<String, Object> priceBreakdown = (Map<String, Object>) raw.get("priceBreakdown");
        Map<String, Object> grossPrice = priceBreakdown != null
            ? (Map<String, Object>) priceBreakdown.get("grossPrice")
            : null;

        return HotelDTO.builder()
            .hotelId(String.valueOf(property.getOrDefault("id", "")))
            .name(String.valueOf(property.getOrDefault("name", "Unknown Hotel")))
            .city(city)
            .country("")
            .stars((Integer) property.getOrDefault("propertyClass", 3))
            .pricePerNight(grossPrice != null
                ? ((Number) grossPrice.getOrDefault("value", 0)).doubleValue()
                : 0.0)
            .rating(((Number) property.getOrDefault("reviewScore", 0)).doubleValue())
            .reviewsCount(((Number) property.getOrDefault("reviewCount", 0)).intValue())
            .imageUrl(String.valueOf(property.getOrDefault("photoUrls", List.of("")).toString()))
            .amenities(List.of("WiFi", "Breakfast"))
            .dataSource("rapidapi")
            .build();
    }
}
