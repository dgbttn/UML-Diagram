package com.model;

public class IllegalNameException extends StructureException {
	/**
	 * The invalid name of the structure
	 */
	private String name;
	
	/**
	 * Package-private constructor
	 * @param name The conflicted name
	 * @param message Error message
	 */
	IllegalNameException(String name, String message) {
		super(message);
		this.name = name;
	}
	
	/**
	 * Package-private constructor
	 * @param name The conflicted name
	 */
	IllegalNameException(String name) {
		this(name, "Illegal name: " + name);
	}
	
	public String getName() {
		return name;
	}
}
