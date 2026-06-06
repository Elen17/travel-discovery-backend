package com.travel.discovery.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Lookup/reference table of accommodation types (Hotel, Villa, Apartment, ...).
 * Referenced by {@link Hotel#getType()} via the {@code type_id} foreign key.
 *
 * <p>This is curated reference data seeded by Flyway (see
 * {@code V11__add_hotel_type_table_type_id_column.sql}); the application only
 * reads it. The in-code {@link com.travel.discovery.entity.enums.HotelTypeCategory}
 * enum mirrors these rows and is used to classify ingested hotels by name.
 */
@Entity
@Table(name = "hotel_type")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HotelType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 30)
    private String name;
}