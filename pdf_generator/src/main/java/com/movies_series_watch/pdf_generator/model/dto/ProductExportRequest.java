package com.movies_series_watch.pdf_generator.model.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ProductExportRequest(
		@NotNull UUID ownerId,
		@NotNull List<@Valid ProductDto> products,
		@NotNull List<@Valid ProductReviewsDto> reviews) {
}
