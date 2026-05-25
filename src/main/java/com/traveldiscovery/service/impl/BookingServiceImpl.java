package com.traveldiscovery.service.impl;

import com.traveldiscovery.dto.request.BookingRequest;
import com.traveldiscovery.dto.response.BookingResponse;
import com.traveldiscovery.dto.response.PageResponse;
import com.traveldiscovery.entity.Booking;
import com.traveldiscovery.entity.Hotel;
import com.traveldiscovery.entity.User;
import com.traveldiscovery.entity.enums.BookingStatus;
import com.traveldiscovery.exception.ForbiddenException;
import com.traveldiscovery.exception.ResourceNotFoundException;
import com.traveldiscovery.repository.BookingRepository;
import com.traveldiscovery.repository.HotelRepository;
import com.traveldiscovery.repository.UserRepository;
import com.traveldiscovery.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingResponse createBooking(Long userId, BookingRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Hotel hotel = hotelRepository.findById(request.getHotelId())
            .orElseThrow(() -> new ResourceNotFoundException("Hotel", request.getHotelId()));

        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        if (nights < 1) {
            throw new IllegalArgumentException("Check-out must be after check-in");
        }

        BigDecimal totalPrice = hotel.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Booking booking = Booking.builder()
            .user(user)
            .hotel(hotel)
            .checkIn(request.getCheckIn())
            .checkOut(request.getCheckOut())
            .guestCount(request.getGuestCount())
            .totalPrice(totalPrice)
            .specialRequests(request.getSpecialRequests())
            .build();

        return toResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> getMyBookings(Long userId, Pageable pageable) {
        Page<Booking> page = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.<BookingResponse>builder()
            .content(page.getContent().stream().map(this::toResponse).toList())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ForbiddenException("Booking is already cancelled");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new ForbiddenException("Completed bookings cannot be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(Instant.now());
        return toResponse(bookingRepository.save(booking));
    }

    private BookingResponse toResponse(Booking b) {
        return BookingResponse.builder()
            .id(b.getId())
            .hotelId(b.getHotel().getId())
            .hotelName(b.getHotel().getName())
            .hotelCity(b.getHotel().getCity())
            .hotelCountry(b.getHotel().getCountry())
            .hotelImageUrl(b.getHotel().getMainImageUrl())
            .hotelLatitude(b.getHotel().getLatitude())
            .hotelLongitude(b.getHotel().getLongitude())
            .checkIn(b.getCheckIn())
            .checkOut(b.getCheckOut())
            .guestCount(b.getGuestCount())
            .totalPrice(b.getTotalPrice())
            .status(b.getStatus().name())
            .createdAt(b.getCreatedAt())
            .build();
    }
}
