package com.review_system.backend.client;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class PdfExportClient {

	private final RestClient restClient;

	public PdfExportClient(RestClient.Builder builder) {
		this.restClient = builder.build();
	}

	public byte[] exportToPdf(String endpointUrl, Object payload) {
		try {
			ResponseEntity<byte[]> response = restClient.post()
					.uri(endpointUrl)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_PDF)
					.body(payload)
					.retrieve()
					.toEntity(byte[].class);

			return response.getBody();
		} catch (RestClientResponseException ex) {
			String message = "Failed to export PDF. Status: %s, Response: %s"
					.formatted(ex.getStatusCode(), ex.getResponseBodyAsString());
			throw new IllegalStateException(message, ex);
		}
	}
}
