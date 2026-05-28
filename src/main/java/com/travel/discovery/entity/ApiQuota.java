package com.travel.discovery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "api_quota")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiQuota {

    @Id
    private String provider; // e.g. "rapidapi"

    private int callsUsed;
    private int callsLimit;
    private LocalDate resetDate;

    public ApiQuota(String provider, int callsLimit) {
        this.provider = provider;
        this.callsUsed = 0;
        this.callsLimit = callsLimit;
        this.resetDate = LocalDate.now().withDayOfMonth(1).plusMonths(1);
    }

    public boolean isActive() {
        return callsUsed < callsLimit;
    }
}
