package com.travel.discovery.repository;

import com.travel.discovery.entity.Favourites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Favourites, Long> {
    List<Favourites> findByUserId(Long userId);
    Optional<Favourites> findByUserIdAndHotelId(Long userId, Long hotelId);
    boolean existsByUserIdAndHotelId(Long userId, Long hotelId);
    void deleteByUserIdAndHotelId(Long userId, Long hotelId);
}
