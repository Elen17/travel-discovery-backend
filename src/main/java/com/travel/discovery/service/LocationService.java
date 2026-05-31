package com.travel.discovery.service;

import com.travel.discovery.dto.response.CityResponse;
import com.travel.discovery.dto.response.CountryResponse;
import com.travel.discovery.entity.City;
import com.travel.discovery.entity.Country;
import com.travel.discovery.exception.BadRequestException;
import com.travel.discovery.exception.ResourceNotFoundException;
import com.travel.discovery.repository.CityRepository;
import com.travel.discovery.repository.CountryRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LocationService {

    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;

    public LocationService(CountryRepository countryRepository, CityRepository cityRepository) {
        this.countryRepository = countryRepository;
        this.cityRepository = cityRepository;
    }

    // ----------------------------------------------------------------
    // ALL COUNTRIES — [{ id, name }], ordered by name
    // ----------------------------------------------------------------
    @Cacheable(value = "countries")
    public List<CountryResponse> getAllCountries() {
        return countryRepository.findAllByOrderByNameAsc().stream()
                .map(country -> CountryResponse.builder()
                        .id(String.valueOf(country.getId()))
                        .name(country.getName())
                        .build())
                .toList();
    }

    // ----------------------------------------------------------------
    // ALL CITIES IN A COUNTRY — [{ id, name }], ordered by name
    // Cache key: "cities:1", "cities:2", ...
    // ----------------------------------------------------------------
    @Cacheable(value = "cities", key = "#countryId")
    public List<CityResponse> getCitiesByCountry(Integer countryId) {
        if (!countryRepository.existsById(countryId)) {
            throw new ResourceNotFoundException("Country", countryId.longValue());
        }
        return cityRepository.findByCountryIdOrderByNameAsc(countryId).stream()
                .map(this::toCityResponse)
                .toList();
    }

    // ----------------------------------------------------------------
    // ALL COUNTRIES + THEIR CITIES — [{ id, name, cities: [{ id, name }] }]
    // ----------------------------------------------------------------
    @Cacheable(value = "countries-cities")
    public List<CountryResponse> getAllCountriesWithCities() {
        Map<Integer, List<CityResponse>> citiesByCountry = cityRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        city -> city.getCountry().getId(),
                        Collectors.mapping(this::toCityResponse, Collectors.toList())));

        return countryRepository.findAllByOrderByNameAsc().stream()
                .map(country -> CountryResponse.builder()
                        .id(String.valueOf(country.getId()))
                        .name(country.getName())
                        .cities(citiesByCountry.getOrDefault(country.getId(), List.of()))
                        .build())
                .toList();
    }

    // ----------------------------------------------------------------
    // VALIDATION — the country/city must be real reference data before we
    // run a hotel search (rejects typos/garbage with a 400 instead of
    // silently returning nothing or burning a RapidAPI call).
    // ----------------------------------------------------------------
    public void validateLocation(String country, String city) {
        if (!StringUtils.hasText(country) || !StringUtils.hasText(city)) {
            throw new BadRequestException("Both 'country' and 'city' are required");
        }
        Country found = countryRepository.findByNameIgnoreCase(country.trim())
                .orElseThrow(() -> new BadRequestException("Unknown country: '" + country + "'"));
        if (!cityRepository.existsByNameIgnoreCaseAndCountryId(city.trim(), found.getId())) {
            throw new BadRequestException(
                    "Unknown city '" + city + "' for country '" + country + "'");
        }
    }

    private CityResponse toCityResponse(City city) {
        return CityResponse.builder()
                .id(String.valueOf(city.getId()))
                .name(city.getName())
                .build();
    }
}