package com.model;

/**
 * Wrapper exception for all other
 * structure-related exceptions.
 */
public class StructureException extends Exception {
	StructureException() {
		super();
	}
	
	StructureException(String message) {
		super(message);
	}
	
	StructureException(Throwable cause) {
		super(cause);
	}
	
	StructureException(String message, Throwable cause) {
		super(message, cause);
	}
}
