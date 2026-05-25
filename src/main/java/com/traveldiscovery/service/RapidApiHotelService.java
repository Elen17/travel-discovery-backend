package com.traveldiscovery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RapidApiHotelService {

    // RestTemplate is injected as a Spring bean (configured with timeouts in RestTemplateConfig)
    private final RestTemplate restTemplate;

    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${rapidapi.host:booking-com15.p.rapidapi.com}")
    private String apiHost;

    private static final String BASE_URL = "https://booking-com15.p.rapidapi.com/api/v1";

    /**
     * Returns the raw hotel list from RapidAPI as a list of maps.
     * Mapping to domain objects is handled by the caller (HotelWrapperService).
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> fetchRawHotels(String city, String checkIn,
                                                     String checkOut, int adults) {
        Map<String, Object> destResult = searchDestination(city);
        List<Map<String, Object>> destData = (List<Map<String, Object>>) destResult.get("data");
        if (destData == null || destData.isEmpty()) {
            log.warn("No destination found for city: {}", city);
            return Collections.emptyList();
        }

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
        return hotels != null ? hotels : Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> searchDestination(String cityName) {
        String url = BASE_URL + "/hotels/searchDestination?query=" + cityName;
        ResponseEntity<Map> response = restTemplate.exchange(
            url, HttpMethod.GET, new HttpEntity<>(headers()), Map.class
        );
        Map<String, Object> body = response.getBody();
        return body != null ? body : Collections.emptyMap();
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", apiHost);
        return headers;
    }
}
