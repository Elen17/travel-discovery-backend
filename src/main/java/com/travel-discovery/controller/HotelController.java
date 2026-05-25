package com.traveldiscovery.controller;

import com.traveldiscovery.dto.HotelDTO;
import com.traveldiscovery.entity.ApiQuota;
import com.traveldiscovery.service.HotelWrapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelWrapperService hotelWrapperService;

    // Search hotels by city + dates (uses RapidAPI if quota active, DB otherwise)
    @GetMapping("/search")
    public ResponseEntity<List<HotelDTO>> searchHotels(
        @RequestParam String city,
        @RequestParam(defaultValue = "") String checkIn,
        @RequestParam(defaultValue = "") String checkOut,
        @RequestParam(defaultValue = "1") int adults
    ) {
        return ResponseEntity.ok(
            hotelWrapperService.searchHotels(city, checkIn, checkOut, adults)
        );
    }

    // Get all locally stored hotels (always from DB)
    @GetMapping
    public ResponseEntity<List<HotelDTO>> getAllHotels() {
        return ResponseEntity.ok(hotelWrapperService.getAllHotels());
    }

    // Quota status — useful for your team to monitor usage
    @GetMapping("/quota")
    public ResponseEntity<ApiQuota> getQuotaStatus() {
        return ResponseEntity.ok(hotelWrapperService.getQuotaStatus());
    }
}
