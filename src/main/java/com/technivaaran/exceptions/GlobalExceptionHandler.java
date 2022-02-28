package com.technivaaran.exceptions;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.technivaaran.dto.OmsResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class, Exception.class })
	protected ResponseEntity<OmsResponse> handleConflict(RuntimeException ex, WebRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		return new ResponseEntity<OmsResponse>(OmsResponse.builder().message(ex.getMessage()).build(), headers,
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = { ConstraintViolationException.class })
	protected ResponseEntity<OmsResponse> handleConstraintViolationException(ConstraintViolationException ex,
			WebRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		List<String> errorList = new ArrayList<>();
		for (ConstraintViolation<? extends Object> error : ex.getConstraintViolations()) {
			errorList.add(error.getMessage());
		}
		return new ResponseEntity<OmsResponse>(OmsResponse.builder().message(errorList.toString()).build(), headers,
				HttpStatus.BAD_REQUEST);
	}

}
