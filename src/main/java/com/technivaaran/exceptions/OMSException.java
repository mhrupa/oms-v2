package com.technivaaran.exceptions;

public class OMSException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OMSException(String errorMessage) {
		super(errorMessage);
	}
}
