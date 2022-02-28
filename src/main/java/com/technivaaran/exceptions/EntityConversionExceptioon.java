package com.technivaaran.exceptions;

public class EntityConversionExceptioon extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EntityConversionExceptioon(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}
}
