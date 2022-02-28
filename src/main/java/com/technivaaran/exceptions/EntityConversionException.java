package com.technivaaran.exceptions;

public class EntityConversionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EntityConversionException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}
}
