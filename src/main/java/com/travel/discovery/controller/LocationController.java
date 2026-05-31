package com.travel.discovery.controller;

import com.travel.discovery.dto.response.CityResponse;
import com.travel.discovery.dto.response.CountryResponse;
import com.travel.discovery.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/countries")
    public ResponseEntity<List<CountryResponse>> getCountries() {
        return ResponseEntity.ok(locationService.getAllCountries());
    }

    // [{ id, name, cities: [{ id, name }] }] — every country with its cities
    @GetMapping("/countries/cities")
    public ResponseEntity<List<CountryResponse>> getCountriesWithCities() {
        return ResponseEntity.ok(locationService.getAllCountriesWithCities());
    }

    @GetMapping("/countries/{countryId}/cities")
    public ResponseEntity<List<CityResponse>> getCitiesByCountry(
        @PathVariable Integer countryId
    ) {
        return ResponseEntity.ok(locationService.getCitiesByCountry(countryId));
    }
}