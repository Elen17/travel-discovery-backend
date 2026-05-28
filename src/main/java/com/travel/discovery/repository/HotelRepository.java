package com.travel.discovery.repository;

import com.travel.discovery.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    boolean existsByExternalId(String externalId);

    // FIX: Added searchHotels JPQL query that HotelServiceImpl.searchHotels() calls.
    // All params are optional (null = not filtered).
    @Query("""
        SELECT h FROM Hotel h
        WHERE (:country IS NULL OR LOWER(h.country) = LOWER(:country))
          AND (:city    IS NULL OR LOWER(h.city)    = LOWER(:city))
          AND (:minPrice IS NULL OR h.pricePerNight >= :minPrice)
          AND (:maxPrice IS NULL OR h.pricePerNight <= :maxPrice)
          AND (:starRating IS NULL OR h.starRating = :starRating)
        """)
    Page<Hotel> searchHotels(
        @Param("country")    String country,
        @Param("city")       String city,
        @Param("minPrice")   BigDecimal minPrice,
        @Param("maxPrice")   BigDecimal maxPrice,
        @Param("starRating") Integer starRating,
        Pageable pageable
    );
}
