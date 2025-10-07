package com.movies_series_watch.pdf_generator.model.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductDto(
		@NotNull UUID id,
		@NotNull UUID ownerId,
		@NotBlank String name,
		String description,
		@NotNull OffsetDateTime createdAt) {
}
