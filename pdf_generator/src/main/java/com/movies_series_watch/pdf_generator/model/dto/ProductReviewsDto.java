package com.movies_series_watch.pdf_generator.model.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductReviewsDto(
		@NotNull UUID productId,
		@NotBlank String productName,
		@NotNull List<@Valid ReviewDto> reviews) {
}
