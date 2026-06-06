package com.travel.discovery.repository;

import com.travel.discovery.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    boolean existsByExternalId(String externalId);

    Optional<Hotel> findByExternalId(String externalId);

    @Query("""
            SELECT h FROM Hotel h
            WHERE (CAST(:country AS string) IS NULL OR LOWER(h.country) = LOWER(CAST(:country AS string)))
              AND (CAST(:city    AS string) IS NULL OR LOWER(h.city)    = LOWER(CAST(:city AS string)))
              AND (:starRating IS NULL OR h.starRating = :starRating)
              AND (:minPrice IS NULL OR h.pricePerNight >= :minPrice)
              AND (:maxPrice IS NULL OR h.pricePerNight <= :maxPrice)
              AND (CAST(:typeName AS string) IS NULL OR LOWER(h.type.name) = LOWER(CAST(:typeName AS string)))
            """)
    Page<Hotel> searchHotels(
            @Param("country") String country,
            @Param("city") String city,
            @Param("starRating") Integer starRating,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("typeName") String typeName,
            Pageable pageable
    );
}
