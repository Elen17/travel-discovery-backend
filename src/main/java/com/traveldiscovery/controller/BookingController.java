package com.traveldiscovery.controller;

import com.traveldiscovery.dto.request.BookingRequest;
import com.traveldiscovery.dto.response.BookingResponse;
import com.traveldiscovery.dto.response.PageResponse;
import com.traveldiscovery.entity.User;
import com.traveldiscovery.exception.ResourceNotFoundException;
import com.traveldiscovery.repository.UserRepository;
import com.traveldiscovery.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody BookingRequest request
    ) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(bookingService.createBooking(userId, request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookingResponse>> getMyBookings(
        @AuthenticationPrincipal UserDetails userDetails,
        @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.ok(bookingService.getMyBookings(userId, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookingResponse> cancelBooking(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long id
    ) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.ok(bookingService.cancelBooking(userId, id));
    }

    private Long resolveUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
            .map(User::getId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
