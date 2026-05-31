package com.travel.discovery.service.impl;

import com.travel.discovery.dto.response.HotelResponse;
import com.travel.discovery.entity.Favourites;
import com.travel.discovery.entity.Hotel;
import com.travel.discovery.entity.User;
import com.travel.discovery.exception.ConflictException;
import com.travel.discovery.exception.ResourceNotFoundException;
import com.travel.discovery.mapper.HotelMapper;
import com.travel.discovery.repository.FavouritesRepository;
import com.travel.discovery.repository.HotelRepository;
import com.travel.discovery.repository.UserRepository;
import com.travel.discovery.service.FavouritesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavouritesServiceImpl implements FavouritesService {

    private final FavouritesRepository favouritesRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final HotelMapper hotelMapper;

    @Override
    public List<HotelResponse> getFavourites(Long userId) {
        return favouritesRepository.findByUserId(userId).stream()
            .map(favourite -> hotelMapper.toResponse(favourite.getHotel()))
            .toList();
    }

    @Override
    @Transactional
    public void addToFavourites(Long userId, Long hotelId) {
        if (favouritesRepository.existsByUserIdAndHotelId(userId, hotelId)) {
            throw new ConflictException("Hotel already in favourites");
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        Hotel hotel = hotelRepository.findById(hotelId)
            .orElseThrow(() -> new ResourceNotFoundException("Hotel", hotelId));

        favouritesRepository.save(Favourites.builder().user(user).hotel(hotel).build());
    }

    @Override
    @Transactional
    public void removeFromFavourites(Long userId, Long hotelId) {
        favouritesRepository.deleteByUserIdAndHotelId(userId, hotelId);
    }
}