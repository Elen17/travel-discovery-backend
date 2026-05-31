package com.travel.discovery.service;

import com.travel.discovery.dto.request.ReviewRequest;
import com.travel.discovery.dto.response.PageResponse;
import com.travel.discovery.dto.response.ReviewResponse;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    PageResponse<ReviewResponse> getHotelReviews(Long hotelId, Pageable pageable);

    /**
     * Creates the caller's review for a hotel, or updates it if one already exists
     * (one review per user per hotel).
     */
    ReviewResponse addOrUpdateReview(Long userId, Long hotelId, ReviewRequest request);

    /** Deletes the caller's own review for a hotel, if present. */
    void deleteMyReview(Long userId, Long hotelId, Long reviewId);
}