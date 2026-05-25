package com.traveldiscovery.controller;

import com.traveldiscovery.dto.response.HotelResponse;
import com.traveldiscovery.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<HotelResponse>> getWishlist(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(wishlistService.getWishlist(userDetails.getUsername()));
    }

    @PostMapping("/{hotelId}")
    public ResponseEntity<Void> addToWishlist(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long hotelId
    ) {
        wishlistService.addToWishlist(userDetails.getUsername(), hotelId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> removeFromWishlist(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long hotelId
    ) {
        wishlistService.removeFromWishlist(userDetails.getUsername(), hotelId);
        return ResponseEntity.noContent().build();
    }
}
