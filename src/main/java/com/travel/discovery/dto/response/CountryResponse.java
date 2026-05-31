package com.travel.discovery.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryResponse {
    private String id;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CityResponse> cities;
}