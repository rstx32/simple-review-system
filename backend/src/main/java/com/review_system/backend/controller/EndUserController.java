package com.review_system.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.review_system.backend.model.Review;
import com.review_system.backend.security.CurrentUserService;
import com.review_system.backend.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/end-user")
@PreAuthorize("hasAnyAuthority('END_USER')")
@Tag(name = "End User Reviews", description = "Endpoints for end users to manage their own reviews")
public class EndUserController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private CurrentUserService currentUserService;

    // show review owned by END_USER
    @GetMapping("/review")
    @Operation(summary = "List reviews authored by the current user")
    public List<Review> getMyReviews() {
        UUID userId = currentUserService.requireUserId();
        return reviewService.getReviewsByReviewerId(userId);
    }

    // edit review
    @PatchMapping("/review/{id}")
    @Operation(summary = "Update a review owned by the current user")
    public Review updateReview(@PathVariable("id") UUID id, @RequestBody Review updatedReview) {
        UUID userId = currentUserService.requireUserId();
        Review existingReview = reviewService.getReviewById(id);
        if (!existingReview.getReviewerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this review.");
        }
        return reviewService.updateReview(id, updatedReview.getRating(), updatedReview.getComment());
    }
}
