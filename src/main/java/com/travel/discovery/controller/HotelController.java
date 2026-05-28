package com.travel.discovery.controller;

import com.travel.discovery.dto.HotelDTO;
import com.travel.discovery.dto.response.HotelResponse;
import com.travel.discovery.dto.response.PageResponse;
import com.travel.discovery.dto.response.ReviewResponse;
import com.travel.discovery.entity.ApiQuota;
import com.travel.discovery.service.HotelService;
import com.travel.discovery.service.HotelWrapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

// FIX: Changed base path from /api/hotels -> /api/v1/hotels
// to match SecurityConfig public permit rule
@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelWrapperService hotelWrapperService;
    private final HotelService hotelService;

    // RapidAPI / local fallback search (quick DTO view for home screen cards)
    @GetMapping("/search")
    public ResponseEntity<List<HotelDTO>> searchHotels(
        @RequestParam String city,
        @RequestParam(defaultValue = "") String checkIn,
        @RequestParam(defaultValue = "") String checkOut,
        @RequestParam(defaultValue = "1") int adults
    ) {
        return ResponseEntity.ok(hotelWrapperService.searchHotels(city, checkIn, checkOut, adults));
    }

    // Full paginated search with filters (used by hotel listing page)
    @GetMapping
    public ResponseEntity<PageResponse<HotelResponse>> getHotels(
        @RequestParam(required = false) String country,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        @RequestParam(required = false) Integer starRating,
        @RequestParam(defaultValue = "0")  int page,
        @RequestParam(defaultValue = "12") int size
    ) {
        return ResponseEntity.ok(hotelService.searchHotels(
            country, city, minPrice, maxPrice, starRating,
            PageRequest.of(page, size, Sort.by("createdAt").descending())
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelResponse> getHotel(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<PageResponse<ReviewResponse>> getReviews(
        @PathVariable Long id,
        @RequestParam(defaultValue = "0")  int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(hotelService.getHotelReviews(
            id, PageRequest.of(page, size, Sort.by("createdAt").descending())
        ));
    }

    @GetMapping("/quota")
    public ResponseEntity<ApiQuota> getQuotaStatus() {
        return ResponseEntity.ok(hotelWrapperService.getQuotaStatus());
    }
}
