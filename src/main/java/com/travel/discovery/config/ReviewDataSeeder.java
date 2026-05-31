package com.travel.discovery.config;

import com.travel.discovery.entity.Hotel;
import com.travel.discovery.entity.Review;
import com.travel.discovery.entity.User;
import com.travel.discovery.repository.HotelRepository;
import com.travel.discovery.repository.ReviewRepository;
import com.travel.discovery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Seeds a handful of reviews (from demo reviewer accounts) for the seeded hotels,
 * so {@code averageRating} / {@code reviewCount} are non-zero out of the box.
 * Runs after {@link HotelDataSeeder}; no-ops once any review exists.
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class ReviewDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    private record DemoReviewer(String fullName, String email) {}

    private static final List<DemoReviewer> REVIEWERS = List.of(
        new DemoReviewer("Alice Martin",   "alice.demo@traveldiscovery.test"),
        new DemoReviewer("Ben Okafor",     "ben.demo@traveldiscovery.test"),
        new DemoReviewer("Carla Rossi",    "carla.demo@traveldiscovery.test")
    );

    // (rating 1–5, comment) pairs cycled across hotels/reviewers.
    private static final List<Object[]> COMMENTS = List.of(
        new Object[]{(short) 5, "Outstanding stay — spotless rooms and incredibly helpful staff."},
        new Object[]{(short) 4, "Great location and comfortable beds. Breakfast could be better."},
        new Object[]{(short) 5, "Beautiful views and a wonderful atmosphere. Would book again."},
        new Object[]{(short) 3, "Decent for the price, but the WiFi was patchy in our room."},
        new Object[]{(short) 4, "Friendly service and a quiet night's sleep. Recommended."}
    );

    @Override
    public void run(String... args) {
        if (reviewRepository.count() > 0) {
            log.info("Reviews already present, skipping review seeding.");
            return;
        }
        List<Hotel> hotels = hotelRepository.findAll();
        if (hotels.isEmpty()) {
            log.info("No hotels to attach demo reviews to, skipping.");
            return;
        }

        List<User> reviewers = REVIEWERS.stream().map(this::ensureUser).toList();

        List<Review> reviews = new ArrayList<>();
        int c = 0;
        for (Hotel hotel : hotels) {
            // Two distinct reviewers per hotel (kept within REVIEWERS to stay unique
            // per (user, hotel)).
            for (int i = 0; i < 2; i++) {
                User reviewer = reviewers.get((c + i) % reviewers.size());
                Object[] comment = COMMENTS.get(c % COMMENTS.size());
                reviews.add(Review.builder()
                    .user(reviewer)
                    .hotel(hotel)
                    .rating((Short) comment[0])
                    .comment((String) comment[1])
                    .build());
                c++;
            }
        }

        reviewRepository.saveAll(reviews);
        log.info("Seeded {} demo reviews across {} hotels.", reviews.size(), hotels.size());
    }

    private User ensureUser(DemoReviewer reviewer) {
        return userRepository.findByEmail(reviewer.email())
            .orElseGet(() -> userRepository.save(User.builder()
                .fullName(reviewer.fullName())
                .email(reviewer.email())
                .passwordHash(passwordEncoder.encode("Demo123!"))
                .build()));
    }
}