package com.travel.discovery.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class FavouriteResponse {
    private Long id;
    private Long hotelId;
    private Instant createdAt;
}
