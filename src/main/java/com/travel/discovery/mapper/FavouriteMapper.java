package com.travel.discovery.mapper;

import com.travel.discovery.dto.response.FavouriteResponse;
import com.travel.discovery.entity.Favourites;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FavouriteMapper {

    public FavouriteResponse toResponse(Favourites favourite) {
        return FavouriteResponse.builder()
            .id(favourite.getId())
            .hotelId(favourite.getHotel().getId())
            .createdAt(favourite.getCreatedAt())
            .build();
    }
}
