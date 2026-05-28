package com.travel.discovery.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "favourites",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "hotel_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favourites extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;
}
