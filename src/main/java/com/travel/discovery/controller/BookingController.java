package com.travel.discovery.controller;

import com.travel.discovery.dto.request.BookingRequest;
import com.travel.discovery.dto.response.BookingResponse;
import com.travel.discovery.dto.response.PageResponse;
import com.travel.discovery.security.CurrentUserService;
import com.travel.discovery.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final CurrentUserService currentUserService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody BookingRequest request
    ) {
        Long userId = currentUserService.getCurrentUserId(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(bookingService.createBooking(userId, request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookingResponse>> getMyBookings(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = currentUserService.getCurrentUserId(userDetails);
        return ResponseEntity.ok(bookingService.getMyBookings(
            userId, PageRequest.of(page, size, Sort.by("createdAt").descending())
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookingResponse> cancelBooking(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long id
    ) {
        Long userId = currentUserService.getCurrentUserId(userDetails);
        return ResponseEntity.ok(bookingService.cancelBooking(userId, id));
    }
}
