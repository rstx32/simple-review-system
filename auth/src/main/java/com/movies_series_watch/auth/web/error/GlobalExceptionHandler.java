package com.movies_series_watch.auth.web.error;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiError> handleResponseStatusException(ResponseStatusException ex) {
		HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
		String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
		return ResponseEntity.status(status).body(new ApiError(status.value(), message));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult().getFieldErrors().stream()
			.map(error -> error.getField() + ": " + error.getDefaultMessage())
			.collect(Collectors.joining("; "));
		if (message.isEmpty()) {
			message = "Validation failed";
		}
		return ResponseEntity.badRequest().body(new ApiError(HttpStatus.BAD_REQUEST.value(), message));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
		String message = ex.getConstraintViolations().stream()
			.map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
			.collect(Collectors.joining("; "));
		if (message.isEmpty()) {
			message = "Constraint violation";
		}
		return ResponseEntity.badRequest().body(new ApiError(HttpStatus.BAD_REQUEST.value(), message));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGenericException(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"));
	}
}
