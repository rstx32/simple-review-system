package com.movies_series_watch.pdf_generator.model.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewDto(
		@NotNull UUID id,
		@NotNull UUID productId,
		@NotNull UUID reviewerId,
		@Min(1) @Max(10) Integer rating,
		String comment,
		@NotNull OffsetDateTime createdAt,
		@NotNull OffsetDateTime updatedAt) {
}
