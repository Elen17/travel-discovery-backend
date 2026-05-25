package com.traveldiscovery.controller;

import com.traveldiscovery.dto.response.HotelResponse;
import com.traveldiscovery.entity.User;
import com.traveldiscovery.entity.Wishlist;
import com.traveldiscovery.exception.ConflictException;
import com.traveldiscovery.exception.ResourceNotFoundException;
import com.traveldiscovery.repository.HotelRepository;
import com.traveldiscovery.repository.UserRepository;
import com.traveldiscovery.repository.WishlistRepository;
import com.traveldiscovery.service.HotelService;
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

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final HotelService hotelService;

    @GetMapping
    public ResponseEntity<List<HotelResponse>> getWishlist(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = resolveUserId(userDetails);
        List<HotelResponse> hotels = wishlistRepository.findByUserId(userId).stream()
            .map(w -> hotelService.getHotelById(w.getHotel().getId()))
            .toList();
        return ResponseEntity.ok(hotels);
    }

    @PostMapping("/{hotelId}")
    public ResponseEntity<Void> addToWishlist(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long hotelId
    ) {
        Long userId = resolveUserId(userDetails);

        if (wishlistRepository.existsByUserIdAndHotelId(userId, hotelId)) {
            throw new ConflictException("Hotel already in wishlist");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        var hotel = hotelRepository.findById(hotelId)
            .orElseThrow(() -> new ResourceNotFoundException("Hotel", hotelId));

        wishlistRepository.save(Wishlist.builder().user(user).hotel(hotel).build());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> removeFromWishlist(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long hotelId
    ) {
        Long userId = resolveUserId(userDetails);
        wishlistRepository.deleteByUserIdAndHotelId(userId, hotelId);
        return ResponseEntity.noContent().build();
    }

    private Long resolveUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
            .map(User::getId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
