package com.traveldiscovery.dto;

import com.traveldiscovery.entity.Booking.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {

    private Long id;
    private String userId;
    private String hotelId;
    private String hotelName;
    private String location;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer guests;
    private Double totalPrice;
    private BookingStatus status;
    private LocalDateTime createdAt;
}
