package com.travel.discovery.repository;

import com.travel.discovery.entity.ApiQuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiQuotaRepository extends JpaRepository<ApiQuota, String> {
}
