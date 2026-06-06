package com.travel.discovery.repository;

import com.travel.discovery.entity.Favourites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavouritesRepository extends JpaRepository<Favourites, Long> {
    List<Favourites> findByUserId(Long userId);
    Optional<Favourites> findByUserIdAndHotelId(Long userId, Long hotelId);
    boolean existsByUserIdAndHotelId(Long userId, Long hotelId);

    // Transaction is provided by FavouritesService (a derived delete needs one).
    void deleteByUserIdAndHotelId(Long userId, Long hotelId);

    // Bulk-deletes a user's favourites on account deletion (transaction provided by UserService).
    @Modifying
    @Query("DELETE FROM Favourites f WHERE f.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
