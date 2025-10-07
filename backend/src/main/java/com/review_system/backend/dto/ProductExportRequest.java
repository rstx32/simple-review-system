package com.review_system.backend.dto;

import java.util.List;
import java.util.UUID;

public record ProductExportRequest(
		UUID ownerId,
		List<ProductEntry> products,
		List<ProductReviews> reviews) {

	public record ProductEntry(
			UUID id,
			UUID ownerId,
			String name,
			String description,
			java.time.OffsetDateTime createdAt) {
	}

	public record ProductReviews(
			UUID productId,
			String productName,
			List<ReviewEntry> reviews) {
	}

	public record ReviewEntry(
			UUID id,
			UUID productId,
			UUID reviewerId,
			Integer rating,
			String comment,
			java.time.OffsetDateTime createdAt,
			java.time.OffsetDateTime updatedAt) {
	}
}
