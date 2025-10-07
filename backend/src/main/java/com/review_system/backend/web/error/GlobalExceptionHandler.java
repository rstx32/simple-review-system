package com.review_system.backend.web.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex) {
		HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
		String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
		if (status.is5xxServerError()) {
			log.error("ResponseStatusException [{}]: {}", status.value(), message, ex);
		} else {
			log.warn("ResponseStatusException [{}]: {}", status.value(), message);
		}
		return ResponseEntity.status(status).body(new ApiError(status.value(), message));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult().getFieldErrors().stream()
			.map(error -> error.getField() + ": " + error.getDefaultMessage())
			.reduce((a, b) -> a + "; " + b)
			.orElse("Validation failed");
		log.warn("Validation failure: {}", message);
		return ResponseEntity.badRequest().body(new ApiError(HttpStatus.BAD_REQUEST.value(), message));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
		String message = ex.getConstraintViolations().stream()
			.map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
			.reduce((a, b) -> a + "; " + b)
			.orElse("Constraint violation");
		log.warn("Constraint violation: {}", message);
		return ResponseEntity.badRequest().body(new ApiError(HttpStatus.BAD_REQUEST.value(), message));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGeneric(Exception ex) {
		log.error("Unhandled exception", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"));
	}
}
