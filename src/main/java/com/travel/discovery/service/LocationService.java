package com.travel.discovery.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class LocationService {

    private final WebClient cscWebClient;
    private final ObjectMapper objectMapper;

    public LocationService(
        @Qualifier("cscWebClient") WebClient cscWebClient,
        ObjectMapper objectMapper
    ) {
        this.cscWebClient = cscWebClient;
        this.objectMapper = objectMapper;
    }

    // ----------------------------------------------------------------
    // ALL COUNTRIES — cached once per day under key "csc:countries"
    // Returns: [{ "id", "name", "iso2", "iso3", "emoji", "currency", ... }]
    // ----------------------------------------------------------------
    @Cacheable(value = "csc:countries")
    public List<Map<String, Object>> getAllCountries() {
        String raw = cscWebClient.get()
            .uri("/countries")
            .retrieve()
            .bodyToMono(String.class)
            .block();

        return parseList(raw);
    }

    // ----------------------------------------------------------------
    // ALL CITIES IN A COUNTRY — cached per country ISO2 code
    // Cache key: "csc:cities:IN", "csc:cities:US", etc.
    // Returns: [{ "id", "name", "state_code", "latitude", "longitude" }]
    // ----------------------------------------------------------------
    @Cacheable(value = "csc:cities", key = "#countryCode.toUpperCase()")
    public List<Map<String, Object>> getCitiesByCountry(String countryCode) {
        String raw = cscWebClient.get()
            .uri("/countries/{code}/cities", countryCode.toUpperCase())
            .retrieve()
            .bodyToMono(String.class)
            .block();

        return parseList(raw);
    }

    // ----------------------------------------------------------------
    // ALL STATES IN A COUNTRY — cached per country ISO2 code
    // Cache key: "csc:states:IN", "csc:states:US", etc.
    // Returns: [{ "id", "name", "iso2", "latitude", "longitude" }]
    // ----------------------------------------------------------------
    @Cacheable(value = "csc:states", key = "#countryCode.toUpperCase()")
    public List<Map<String, Object>> getStatesByCountry(String countryCode) {
        String raw = cscWebClient.get()
            .uri("/countries/{code}/states", countryCode.toUpperCase())
            .retrieve()
            .bodyToMono(String.class)
            .block();

        return parseList(raw);
    }

    // ----------------------------------------------------------------
    // ALL CITIES IN A STATE — cached per "countryCode:stateCode"
    // Cache key: "csc:state-cities:IN:GJ", etc.
    // ----------------------------------------------------------------
    @Cacheable(value = "csc:state-cities", key = "#countryCode.toUpperCase() + ':' + #stateCode.toUpperCase()")
    public List<Map<String, Object>> getCitiesByState(String countryCode, String stateCode) {
        String raw = cscWebClient.get()
            .uri("/countries/{country}/states/{state}/cities",
                countryCode.toUpperCase(), stateCode.toUpperCase())
            .retrieve()
            .bodyToMono(String.class)
            .block();

        return parseList(raw);
    }

    // ----------------------------------------------------------------
    // Helper
    // ----------------------------------------------------------------
    private List<Map<String, Object>> parseList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSC API response: " + e.getMessage(), e);
        }
    }
}
