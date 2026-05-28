package com.travel.discovery.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class BookingResponse {
    private Long id;
    private Long hotelId;
    private String hotelName;
    private String hotelCity;
    private String hotelCountry;
    private String hotelImageUrl;
    private BigDecimal hotelLatitude;
    private BigDecimal hotelLongitude;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer guestCount;
    private BigDecimal totalPrice;
    private String status;
    private Instant createdAt;
}
