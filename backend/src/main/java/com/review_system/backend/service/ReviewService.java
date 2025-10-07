package com.review_system.backend.service;

import com.review_system.backend.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReviewService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Review> getAllReviews() {
        String sql = "SELECT * FROM reviews";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Review.class));
    }

    public Review getReviewById(Long id) {
        String sql = "SELECT * FROM reviews WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Review.class), id);
    }

    public Review getReviewById(UUID id) {
        String sql = "SELECT * FROM reviews WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Review.class), id);
    }

    public List<Review> getReviewsByProductId(UUID productId) {
        String sql = "SELECT * FROM reviews WHERE product_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Review.class), productId);
    }

    public List<Review> getReviewsByReviewerId(UUID reviewerId) {
        String sql = "SELECT * FROM reviews WHERE reviewer_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Review.class), reviewerId);
    }

    public Optional<Review> findReviewByProductAndReviewer(UUID productId, UUID reviewerId) {
        String sql = "SELECT * FROM reviews WHERE product_id = ? AND reviewer_id = ?";
        List<Review> results = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Review.class), productId,
                reviewerId);
        return results.stream().findFirst();
    }

    public Review createReview(Review review) {
        String sql = "INSERT INTO reviews (id, product_id, reviewer_id, rating, comment, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING *";
        UUID reviewId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        Review createdReview = jdbcTemplate.queryForObject(
                sql,
                new BeanPropertyRowMapper<>(Review.class),
                reviewId,
                review.getProductId(),
                review.getReviewerId(),
                review.getRating(),
                review.getComment(),
                Timestamp.from(now.toInstant()),
                Timestamp.from(now.toInstant()));
        return createdReview;
    }

    public Review updateReview(UUID id, Integer rating, String comment) {
        String sql = "UPDATE reviews SET rating = COALESCE(?, rating), comment = COALESCE(?, comment), updated_at = ? "
                +
                "WHERE id = ? RETURNING *";
        OffsetDateTime now = OffsetDateTime.now();
        return jdbcTemplate.queryForObject(
                sql,
                new BeanPropertyRowMapper<>(Review.class),
                rating,
                comment,
                Timestamp.from(now.toInstant()),
                id);
    }
}
