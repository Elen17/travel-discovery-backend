package com.travel.discovery.controller;

import com.travel.discovery.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/countries")
    public ResponseEntity<List<Map<String, Object>>> getCountries() {
        return ResponseEntity.ok(locationService.getAllCountries());
    }

    @GetMapping("/countries/{countryCode}/cities")
    public ResponseEntity<List<Map<String, Object>>> getCitiesByCountry(
        @PathVariable String countryCode
    ) {
        return ResponseEntity.ok(locationService.getCitiesByCountry(countryCode));
    }

    @GetMapping("/countries/{countryCode}/states")
    public ResponseEntity<List<Map<String, Object>>> getStatesByCountry(
        @PathVariable String countryCode
    ) {
        return ResponseEntity.ok(locationService.getStatesByCountry(countryCode));
    }

    @GetMapping("/countries/{countryCode}/states/{stateCode}/cities")
    public ResponseEntity<List<Map<String, Object>>> getCitiesByState(
        @PathVariable String countryCode,
        @PathVariable String stateCode
    ) {
        return ResponseEntity.ok(locationService.getCitiesByState(countryCode, stateCode));
    }
}
