package com.travel.discovery.controller;

import com.travel.discovery.dto.request.ReviewRequest;
import com.travel.discovery.dto.response.HotelResponse;
import com.travel.discovery.dto.response.PageResponse;
import com.travel.discovery.dto.response.ReviewResponse;
import com.travel.discovery.entity.enums.HotelTypeCategory;
import com.travel.discovery.security.CurrentUserService;
import com.travel.discovery.service.HotelService;
import com.travel.discovery.service.HotelWrapperService;
import com.travel.discovery.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final ReviewService reviewService;
    private final CurrentUserService currentUserService;

    // RapidAPI-backed search: ingests fresh results into the DB (within quota), then
    // returns DB-backed hotels (bookable Long id, same shape as the rest of the app).
    @GetMapping("/search")
    public ResponseEntity<List<HotelResponse>> searchHotels(
            @RequestParam String country,
            @RequestParam String city,
            @RequestParam(defaultValue = "") String checkIn,
            @RequestParam(defaultValue = "") String checkOut,
            @RequestParam(defaultValue = "1") int adults
    ) {
        return ResponseEntity.ok(
                hotelWrapperService.searchHotels(country, city, checkIn, checkOut, adults)
        );
    }

    // Full paginated search with filters (used by hotel listing page)
    @GetMapping
    public ResponseEntity<PageResponse<HotelResponse>> getHotels(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer starRating,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) HotelTypeCategory type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return ResponseEntity.ok(hotelService.searchHotels(
                country, city, starRating, minPrice, maxPrice, type,
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelResponse> getHotel(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    // Public: anyone can read a hotel's reviews.
    @GetMapping("/{id}/reviews")
    public ResponseEntity<PageResponse<ReviewResponse>> getReviews(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(reviewService.getHotelReviews(
                id, PageRequest.of(page, size, Sort.by("createdAt").descending())
        ));
    }

    // Authenticated: create or update the caller's review for this hotel (one per user).
    @PostMapping("/{id}/reviews")
    public ResponseEntity<ReviewResponse> addReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request
    ) {
        Long userId = currentUserService.getCurrentUserId(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.addOrUpdateReview(userId, id, request));
    }

    // Authenticated: delete the caller's own review for this hotel.
    @DeleteMapping("/{hotelId}/reviews/{id}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable(name = "hotelId") Long hotelId,
            @PathVariable Long id
    ) {
        Long userId = currentUserService.getCurrentUserId(userDetails);
        reviewService.deleteMyReview(userId, hotelId, id);
        return ResponseEntity.noContent().build();
    }

}
