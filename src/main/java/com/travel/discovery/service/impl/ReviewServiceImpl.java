package com.travel.discovery.service.impl;

import com.travel.discovery.dto.request.ReviewRequest;
import com.travel.discovery.dto.response.PageResponse;
import com.travel.discovery.dto.response.ReviewResponse;
import com.travel.discovery.entity.Hotel;
import com.travel.discovery.entity.Review;
import com.travel.discovery.entity.User;
import com.travel.discovery.exception.ResourceNotFoundException;
import com.travel.discovery.repository.HotelRepository;
import com.travel.discovery.repository.ReviewRepository;
import com.travel.discovery.repository.UserRepository;
import com.travel.discovery.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;

    @Override
    public PageResponse<ReviewResponse> getHotelReviews(Long hotelId, Pageable pageable) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new ResourceNotFoundException("Hotel", hotelId);
        }
        Page<Review> page = reviewRepository.findByHotelId(hotelId, pageable);
        return PageResponse.<ReviewResponse>builder()
            .content(page.getContent().stream().map(this::toResponse).toList())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }

    @Override
    @Transactional
    public ReviewResponse addOrUpdateReview(Long userId, Long hotelId, ReviewRequest request) {
        Hotel hotel = hotelRepository.findById(hotelId)
            .orElseThrow(() -> new ResourceNotFoundException("Hotel", hotelId));

        // Upsert: one review per user per hotel (enforced by UQ_REVIEWS_USER_HOTEL).
        Review review = reviewRepository.findByUserIdAndHotelId(userId, hotelId)
            .orElseGet(() -> {
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", userId));
                return Review.builder().user(user).hotel(hotel).build();
            });

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        return toResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public void deleteMyReview(Long userId, Long hotelId, Long reviewId) {
        if (!reviewRepository.existsByUserIdAndHotelIdAndId(userId, hotelId, reviewId)) {
            throw new ResourceNotFoundException(
                "Review not found for hotel " + hotelId);
        }
        reviewRepository.deleteByUserIdAndHotelIdAndId(userId, hotelId, reviewId);
    }

    private ReviewResponse toResponse(Review r) {
        return ReviewResponse.builder()
            .id(r.getId())
            .userId(r.getUser().getId())
            .reviewerName(r.getUser().getFullName())
            .reviewerAvatarUrl(r.getUser().getAvatarUrl())
            .rating(r.getRating())
            .comment(r.getComment())
            .createdAt(r.getCreatedAt())
            .build();
    }
}