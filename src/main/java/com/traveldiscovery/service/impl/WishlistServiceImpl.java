package com.traveldiscovery.service.impl;

import com.traveldiscovery.dto.response.HotelResponse;
import com.traveldiscovery.entity.User;
import com.traveldiscovery.entity.Wishlist;
import com.traveldiscovery.exception.ConflictException;
import com.traveldiscovery.exception.ResourceNotFoundException;
import com.traveldiscovery.repository.HotelRepository;
import com.traveldiscovery.repository.UserRepository;
import com.traveldiscovery.repository.WishlistRepository;
import com.traveldiscovery.service.HotelService;
import com.traveldiscovery.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final HotelService hotelService;

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponse> getWishlist(String email) {
        Long userId = resolveUserId(email);
        return wishlistRepository.findByUserId(userId).stream()
            .map(w -> hotelService.getHotelById(w.getHotel().getId()))
            .toList();
    }

    @Override
    @Transactional
    public void addToWishlist(String email, Long hotelId) {
        Long userId = resolveUserId(email);
        if (wishlistRepository.existsByUserIdAndHotelId(userId, hotelId)) {
            throw new ConflictException("Hotel already in wishlist");
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        var hotel = hotelRepository.findById(hotelId)
            .orElseThrow(() -> new ResourceNotFoundException("Hotel", hotelId));
        wishlistRepository.save(Wishlist.builder().user(user).hotel(hotel).build());
    }

    @Override
    @Transactional
    public void removeFromWishlist(String email, Long hotelId) {
        Long userId = resolveUserId(email);
        wishlistRepository.deleteByUserIdAndHotelId(userId, hotelId);
    }

    private Long resolveUserId(String email) {
        return userRepository.findByEmail(email)
            .map(User::getId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
