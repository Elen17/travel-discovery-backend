package com.travel.discovery.dto;

import com.travel.discovery.entity.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

// FIX: Now uses com.travel.discovery.entity.enums.BookingStatus (not the old inner class)
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BookingDTO {
    private Long id;
    private Long userId;
    private Long hotelId;
    private String hotelName;
    private String hotelCity;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer guestCount;
    private BigDecimal totalPrice;
    private BookingStatus status;
    private Instant createdAt;
}
