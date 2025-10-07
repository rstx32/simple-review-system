package com.review_system.backend.model;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class Review {
    private UUID id;
    private UUID productId;
    private UUID reviewerId;
    private Integer rating;
    private String comment;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}