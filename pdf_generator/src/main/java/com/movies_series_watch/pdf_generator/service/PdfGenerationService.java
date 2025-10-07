package com.movies_series_watch.pdf_generator.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.movies_series_watch.pdf_generator.model.dto.ProductDto;
import com.movies_series_watch.pdf_generator.model.dto.ProductExportRequest;
import com.movies_series_watch.pdf_generator.model.dto.ProductReviewsDto;
import com.movies_series_watch.pdf_generator.model.dto.ReviewDto;

@Service
public class PdfGenerationService {

	private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
	private static final Font SECTION_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
	private static final Font BODY_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10);
	private static final Font SMALL_ITALIC_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.ITALIC);
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm xxx");

	public byte[] generateProductExport(ProductExportRequest request) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			Document document = new Document(PageSize.A4, 36, 36, 54, 36);
			PdfWriter.getInstance(document, baos);

			document.open();

			addTitleSection(document, request.ownerId());

			Map<UUID, ProductReviewsDto> reviewsByProduct = toProductReviewMap(request.reviews());

			int index = 1;
			for (ProductDto product : request.products()) {
				addProductSection(document, product, reviewsByProduct.get(product.id()), index++);
			}

			document.close();
			return baos.toByteArray();
		} catch (DocumentException | IOException ex) {
			throw new IllegalStateException("Failed to generate PDF document", ex);
		}
	}

	private void addTitleSection(Document document, UUID ownerId) throws DocumentException {
		Paragraph title = new Paragraph("Product Portfolio", TITLE_FONT);
		title.setSpacingAfter(8f);
		document.add(title);

		Paragraph ownerParagraph = new Paragraph("Owner ID: " + ownerId, BODY_FONT);
		ownerParagraph.setSpacingAfter(16f);
		document.add(ownerParagraph);
	}

	private void addProductSection(Document document, ProductDto product, ProductReviewsDto productReviews, int index)
			throws DocumentException {
		Paragraph sectionHeader = new Paragraph(index + ". " + product.name(), SECTION_FONT);
		sectionHeader.setSpacingBefore(6f);
		sectionHeader.setSpacingAfter(4f);
		document.add(sectionHeader);

		if (product.description() != null && !product.description().isBlank()) {
			Paragraph description = new Paragraph(product.description(), BODY_FONT);
			description.setSpacingAfter(4f);
			document.add(description);
		}

		Paragraph metadata = new Paragraph(
				"Created: " + formatDate(product.createdAt()) + "  |  Product ID: " + product.id(), SMALL_ITALIC_FONT);
		metadata.setSpacingAfter(8f);
		document.add(metadata);

		List<ReviewDto> reviews = productReviews != null ? productReviews.reviews() : Collections.emptyList();
		if (reviews.isEmpty()) {
			Paragraph noReviews = new Paragraph("No reviews yet.", BODY_FONT);
			noReviews.setSpacingAfter(10f);
			document.add(noReviews);
			return;
		}

		PdfPTable table = new PdfPTable(new float[] { 2f, 5f, 2f, 3f });
		table.setWidthPercentage(100);
		addReviewTableHeader(table);
		reviews.forEach(review -> addReviewRow(table, review));

		table.setSpacingAfter(12f);
		document.add(table);
		document.add(Chunk.NEWLINE);
	}

	private void addReviewTableHeader(PdfPTable table) {
		table.addCell(createHeaderCell("Reviewer ID"));
		table.addCell(createHeaderCell("Comment"));
		table.addCell(createHeaderCell("Rating"));
		table.addCell(createHeaderCell("Updated"));
	}

	private void addReviewRow(PdfPTable table, ReviewDto review) {
		table.addCell(createBodyCell(review.reviewerId().toString()));
		table.addCell(createBodyCell(review.comment() != null ? review.comment() : "-"));
		table.addCell(createBodyCell(review.rating() != null ? review.rating().toString() : "-"));
		table.addCell(createBodyCell(formatDate(review.updatedAt())));
	}

	private PdfPCell createHeaderCell(String text) {
		PdfPCell cell = new PdfPCell(new Phrase(text, SECTION_FONT));
		cell.setPadding(6f);
		return cell;
	}

	private PdfPCell createBodyCell(String text) {
		PdfPCell cell = new PdfPCell(new Phrase(text, BODY_FONT));
		cell.setPadding(5f);
		return cell;
	}

	private Map<UUID, ProductReviewsDto> toProductReviewMap(List<ProductReviewsDto> reviews) {
		return reviews.stream()
				.collect(Collectors.toMap(
						ProductReviewsDto::productId,
						entry -> entry,
						(existing, replacement) -> existing,
						LinkedHashMap::new));
	}

	private String formatDate(OffsetDateTime timestamp) {
		return timestamp != null ? DATE_FORMATTER.format(timestamp) : "-";
	}
}
