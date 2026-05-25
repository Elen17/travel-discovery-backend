package com.traveldiscovery.repository;

import com.traveldiscovery.entity.ApiQuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiQuotaRepository extends JpaRepository<ApiQuota, String> {
}
