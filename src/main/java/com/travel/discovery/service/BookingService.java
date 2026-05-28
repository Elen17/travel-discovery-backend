package com.travel.discovery.service;

import com.travel.discovery.dto.request.BookingRequest;
import com.travel.discovery.dto.response.BookingResponse;
import com.travel.discovery.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingResponse createBooking(Long userId, BookingRequest request);
    PageResponse<BookingResponse> getMyBookings(Long userId, Pageable pageable);
    BookingResponse cancelBooking(Long userId, Long bookingId);
}
