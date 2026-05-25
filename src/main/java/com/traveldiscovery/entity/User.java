package com.traveldiscovery.entity;

import com.traveldiscovery.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "home_country")
    private String homeCountry;

    @Column(name = "preferred_currency", length = 3)
    @Builder.Default
    private String preferredCurrency = "USD";

    @Column(name = "preferred_language", length = 5)
    @Builder.Default
    private String preferredLanguage = "en";

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Column(name = "google_id")
    private String googleId;

    @Column(name = "notification_bookings")
    @Builder.Default
    private Boolean notificationBookings = true;

    @Column(name = "notification_inspiration")
    @Builder.Default
    private Boolean notificationInspiration = false;
}
