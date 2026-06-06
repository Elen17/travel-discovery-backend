package com.travel.discovery.repository;

import com.travel.discovery.entity.HotelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HotelTypeRepository extends JpaRepository<HotelType, Integer> {

    Optional<HotelType> findByNameIgnoreCase(String name);
}