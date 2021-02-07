package com.model;

public class DuplicateStructureException extends StructureException {
	/**
	 * Package-private constructor
	 * @param message Error message
	 */
	DuplicateStructureException(String message) {
		super(message);
	}
	
	/**
	 * Package-private constructor
	 */
	DuplicateStructureException() {
		super();
	}
}
