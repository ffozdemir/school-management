package com.ffozdemir.schoolmanagement.exception;

public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException(
				String message) {
		super(message);
	}
}
