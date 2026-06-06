package com.travel.discovery.config;

import com.travel.discovery.entity.enums.HotelTypeCategory;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Binds the {@code type} request param to {@link HotelTypeCategory} leniently
 * (case-insensitive, display name or constant name). Spring's default enum
 * converter is exact/case-sensitive, which rejected values like {@code hotel}.
 *
 * <p>Registered automatically: Spring Boot adds {@link Converter} beans to the
 * MVC conversion service.
 */
@Component
public class StringToHotelTypeCategoryConverter implements Converter<String, HotelTypeCategory> {

    @Override
    public HotelTypeCategory convert(@NonNull String source) {
        return HotelTypeCategory.fromParam(source);
    }
}