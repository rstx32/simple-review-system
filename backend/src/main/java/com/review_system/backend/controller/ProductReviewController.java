package com.review_system.backend.controller;

import com.review_system.backend.model.Product;
import com.review_system.backend.service.ProductService;
import com.review_system.backend.service.ReviewService;
import com.review_system.backend.model.Review;
import com.review_system.backend.security.CurrentUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Product Reviews", description = "Product catalogue and review endpoints")
public class ProductReviewController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private CurrentUserService currentUserService;

    // PRODUCT ZONE
    // Get all products
    @GetMapping("/product")
    @Operation(summary = "List all products")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // Get product by id
    @GetMapping("/product/{id}")
    @Operation(summary = "Get a product by its identifier")
    public Product getProductById(@PathVariable("id") UUID id) {
        return productService.getProductById(id);
    }

    // REVIEW ZONE
    // Get all review of product
    @GetMapping("/product/{id}/review")
    @Operation(summary = "List all reviews for a product")
    public List<Review> getReviewsForProduct(@PathVariable("id") UUID id) {
        return reviewService.getReviewsByProductId(id);
    }

    // Get a review by id
    @GetMapping("/review/{id}")
    @Operation(summary = "Get a single review by its identifier")
    public Review getReviewById(@PathVariable("id") UUID id) {
        return reviewService.getReviewById(id);
    }

    // Create review (require login)
    @PostMapping("/product/{id}/review")
    @Operation(summary = "Create a review for a product (one per user)")
    @PreAuthorize("hasAnyAuthority('END_USER')")
    public Review createReview(@PathVariable("id") UUID productId, @RequestBody Review review) {
        UUID userId = currentUserService.requireUserId();
        if (reviewService.findReviewByProductAndReviewer(productId, userId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Review already exists for this product.");
        }
        review.setReviewerId(userId);
        review.setProductId(productId);
        return reviewService.createReview(review);
    }

}
