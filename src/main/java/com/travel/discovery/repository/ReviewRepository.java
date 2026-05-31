package com.travel.discovery.repository;

import com.travel.discovery.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByHotelId(Long hotelId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.hotel.id = :hotelId")
    Optional<Double> findAverageRatingByHotelId(@Param("hotelId") Long hotelId);

    long countByHotelId(Long hotelId);

    boolean existsByUserIdAndHotelIdAndId(Long userId, Long hotelId, Long reviewId);

    Optional<Review> findByUserIdAndHotelId(Long userId, Long hotelId);

    void deleteByUserIdAndHotelIdAndId(Long userId, Long hotelId, Long reviewId);
}
