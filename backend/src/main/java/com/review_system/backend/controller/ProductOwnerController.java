package com.review_system.backend.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.review_system.backend.client.PdfExportClient;
import com.review_system.backend.dto.ProductExportRequest;
import com.review_system.backend.model.Product;
import com.review_system.backend.model.Review;
import com.review_system.backend.security.CurrentUserService;
import com.review_system.backend.security.UserPrincipal;
import com.review_system.backend.service.ProductService;
import com.review_system.backend.service.ReviewService;

@RestController
@RequestMapping("/api/product-owner")
@PreAuthorize("hasAnyAuthority('PRODUCT_OWNER')")
public class ProductOwnerController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private PdfExportClient pdfExportClient;

    @Value("${app.export.pdf-endpoint}")
    private String pdfExportEndpoint;

    // Get all products owned by the current PRODUCT_OWNER
    @GetMapping("/product")
    public List<Product> getMyProducts() {
        UUID ownerId = currentUserService.requireUserId();
        return productService.getProductsByOwner(ownerId);
    }

    // download PDF for summary product + review
    @GetMapping("/product/export/pdf")
    public ResponseEntity<byte[]> exportMyProductsToPdf() {
        UUID ownerId = currentUserService.requireUserId();
        List<Product> products = productService.getProductsByOwner(ownerId);
        Map<UUID, List<Review>> reviewsByProduct = products.stream()
                .collect(Collectors.toMap(
                        Product::getId,
                        product -> reviewService.getReviewsByProductId(product.getId())));

        List<ProductExportRequest.ProductEntry> productEntries = products.stream()
                .map(product -> new ProductExportRequest.ProductEntry(
                        product.getId(),
                        product.getOwnerId(),
                        product.getName(),
                        product.getDescription(),
                        product.getCreatedAt()))
                .toList();

        List<ProductExportRequest.ProductReviews> reviewGroups = products.stream()
                .map(product -> {
                    List<ProductExportRequest.ReviewEntry> reviewEntries = reviewsByProduct
                            .getOrDefault(product.getId(), List.of())
                            .stream()
                            .map(review -> new ProductExportRequest.ReviewEntry(
                                    review.getId(),
                                    review.getProductId(),
                                    review.getReviewerId(),
                                    review.getRating(),
                                    review.getComment(),
                                    review.getCreatedAt(),
                                    review.getUpdatedAt()))
                            .toList();
                    return new ProductExportRequest.ProductReviews(
                            product.getId(),
                            product.getName(),
                            reviewEntries);
                })
                .toList();

        ProductExportRequest payload = new ProductExportRequest(
                ownerId,
                productEntries,
                reviewGroups);

        byte[] pdfBytes = pdfExportClient.exportToPdf(pdfExportEndpoint, payload);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "products-export.pdf");
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfBytes);
    }

    // Create new product (only for PRODUCT_OWNER)
    @PostMapping("/product")
    public Product createProduct(@RequestBody Product product) {
        UserPrincipal principal = currentUserService.getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));
        UUID ownerId = principal.userId();
        return productService.createProduct(product, ownerId);
    }

    // Delete product by id (only owner can delete)
    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") UUID id) {
        try {
            UserPrincipal principal = currentUserService.getCurrentUser()
                    .orElseThrow(() -> new IllegalStateException("User not authenticated"));
            UUID ownerId = principal.userId();
            Product product = productService.getProductById(id);
            if (!product.getOwnerId().equals(ownerId)) {
                return ResponseEntity.status(403).body("You are not the owner of this product.");
            }
            productService.deleteProduct(id);
            return ResponseEntity.ok("Product deleted successfully.");
        } catch (org.springframework.dao.EmptyResultDataAccessException | IllegalStateException e) {
            return ResponseEntity.status(404).body("Product not found.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete product.");
        }
    }
}
