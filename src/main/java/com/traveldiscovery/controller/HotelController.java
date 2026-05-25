package com.traveldiscovery.controller;

import com.traveldiscovery.dto.response.HotelResponse;
import com.traveldiscovery.dto.response.PageResponse;
import com.traveldiscovery.dto.response.ReviewResponse;
import com.traveldiscovery.entity.ApiQuota;
import com.traveldiscovery.service.HotelService;
import com.traveldiscovery.service.HotelWrapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;
    private final HotelWrapperService hotelWrapperService;

    // Paginated hotel search — served from local DB (populated via RapidAPI sync)
    @GetMapping
    public ResponseEntity<PageResponse<HotelResponse>> searchHotels(
        @RequestParam(required = false) String country,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        @RequestParam(required = false) Integer starRating,
        @PageableDefault(size = 12, sort = "pricePerNight") Pageable pageable
    ) {
        return ResponseEntity.ok(
            hotelService.searchHotels(country, city, minPrice, maxPrice, starRating, pageable)
        );
    }

    // Get single hotel detail
    @GetMapping("/{id}")
    public ResponseEntity<HotelResponse> getHotel(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    // Get reviews for a hotel
    @GetMapping("/{id}/reviews")
    public ResponseEntity<PageResponse<ReviewResponse>> getHotelReviews(
        @PathVariable Long id,
        @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(hotelService.getHotelReviews(id, pageable));
    }

    // Trigger a RapidAPI sync for a city — populates local DB with live data
    @PostMapping("/sync")
    public ResponseEntity<String> syncFromRapidApi(
        @RequestParam String city,
        @RequestParam(defaultValue = "") String checkIn,
        @RequestParam(defaultValue = "") String checkOut,
        @RequestParam(defaultValue = "1") int adults
    ) {
        int synced = hotelWrapperService.syncCity(city, checkIn, checkOut, adults);
        return ResponseEntity.ok("Synced " + synced + " hotels for city: " + city);
    }

    // RapidAPI quota status — monitor monthly usage
    @GetMapping("/quota")
    public ResponseEntity<ApiQuota> getQuotaStatus() {
        return ResponseEntity.ok(hotelWrapperService.getQuotaStatus());
    }
}
