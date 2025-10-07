package com.review_system.backend.model;

import lombok.Data;
import java.util.UUID;
import java.time.OffsetDateTime;

@Data
public class Product {
    private UUID id;
    private UUID ownerId;
    private String name;
    private String description;
    private OffsetDateTime createdAt;
}