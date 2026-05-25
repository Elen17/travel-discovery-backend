package com.traveldiscovery.service;

import com.traveldiscovery.dto.BookingDTO;
import com.traveldiscovery.entity.Booking;
import com.traveldiscovery.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingDTO createBooking(BookingDTO dto) {
        Booking booking = new Booking();
        booking.setUserId(dto.getUserId());
        booking.setHotelId(dto.getHotelId());
        booking.setHotelName(dto.getHotelName());
        booking.setLocation(dto.getLocation());
        booking.setCheckIn(dto.getCheckIn());
        booking.setCheckOut(dto.getCheckOut());
        booking.setGuests(dto.getGuests());
        booking.setTotalPrice(dto.getTotalPrice());
        booking.setStatus(Booking.BookingStatus.PENDING);

        return mapToDTO(bookingRepository.save(booking));
    }

    public List<BookingDTO> getUserBookings(String userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(this::mapToDTO)
            .toList();
    }

    public BookingDTO cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking not found: " + id));
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        return mapToDTO(bookingRepository.save(booking));
    }

    public BookingDTO confirmBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking not found: " + id));
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        return mapToDTO(bookingRepository.save(booking));
    }

    private BookingDTO mapToDTO(Booking booking) {
        return BookingDTO.builder()
            .id(booking.getId())
            .userId(booking.getUserId())
            .hotelId(booking.getHotelId())
            .hotelName(booking.getHotelName())
            .location(booking.getLocation())
            .checkIn(booking.getCheckIn())
            .checkOut(booking.getCheckOut())
            .guests(booking.getGuests())
            .totalPrice(booking.getTotalPrice())
            .status(booking.getStatus())
            .createdAt(booking.getCreatedAt())
            .build();
    }
}
