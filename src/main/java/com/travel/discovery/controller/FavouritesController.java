package com.travel.discovery.controller;

import com.travel.discovery.dto.response.FavouriteResponse;
import com.travel.discovery.dto.response.HotelResponse;
import com.travel.discovery.security.CurrentUserService;
import com.travel.discovery.service.FavouritesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favourites")
@RequiredArgsConstructor
public class FavouritesController {

    private final FavouritesService favouritesService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public ResponseEntity<List<FavouriteResponse>> getFavourites(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = currentUserService.getCurrentUserId(userDetails);
        return ResponseEntity.ok(favouritesService.getFavourites(userId));
    }

    @PostMapping("/{hotelId}")
    public ResponseEntity<Void> addToFavourites(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long hotelId
    ) {
        Long userId = currentUserService.getCurrentUserId(userDetails);
        favouritesService.addToFavourites(userId, hotelId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> removeFromFavourites(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long hotelId
    ) {
        Long userId = currentUserService.getCurrentUserId(userDetails);
        favouritesService.removeFromFavourites(userId, hotelId);
        return ResponseEntity.noContent().build();
    }
}
