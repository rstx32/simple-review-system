package com.movies_series_watch.pdf_generator.controller;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movies_series_watch.pdf_generator.model.dto.ProductExportRequest;
import com.movies_series_watch.pdf_generator.service.PdfGenerationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/export")
@Validated
public class PdfExportController {

	private final PdfGenerationService pdfGenerationService;

	public PdfExportController(PdfGenerationService pdfGenerationService) {
		this.pdfGenerationService = pdfGenerationService;
	}

	@PostMapping(path = "/pdf", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> exportProducts(@Valid @RequestBody ProductExportRequest request) {
		byte[] pdfBytes = pdfGenerationService.generateProductExport(request);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDisposition(ContentDisposition.attachment()
				.filename("products-" + request.ownerId() + ".pdf")
				.build());

		return ResponseEntity
				.ok()
				.headers(headers)
				.body(pdfBytes);
	}
}
