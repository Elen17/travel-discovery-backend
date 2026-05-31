package com.travel.discovery.repository;

import com.travel.discovery.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {
    List<City> findByCountryIdOrderByNameAsc(Integer countryId);

    boolean existsByNameIgnoreCaseAndCountryId(String name, Integer countryId);
}