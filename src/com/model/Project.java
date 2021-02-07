package com.model;

import java.util.Collection;

/**
 * A class to manage all structure related operations
 */
public interface Project {
	public static final Project instance = new JavaProject();
	
	public static Project getInstance() {
		return instance;
	}
	
	public Class.Builder getClassBuilder();
	
	public Interface.Builder getInterfaceBuilder();
	
	public Method.Builder getMethodBuilder();
	
	public Property.Builder getPropertyBuilder();
	
	/**
	 * Validate and add a Class to the project
	 * @param c class
	 * @throws DuplicateStructureException if name is not globally unique
	 */
	public void add(Class c) throws StructureException;
	
	/**
	 * @param name name of Class
	 * @return the Class with that name in the project,
	 *         or null if not exist
	 */
	public Class getClassByName(String name);
	
	/**
	 * Replace the old class with a new one
	 * This is the only way to update a Class,
	 * since a Class is immutable outside of package
	 * @param oldCl old class
	 * @param newCl new class
	 * @throws StructureException if new class's state is not valid
	 */
	public void replace(Class oldCl, Class newCl) throws StructureException;
	
	/**
	 * Remove class from the project
	 * @param c class
	 */
	public void remove(Class c);
	
	/**
	 * Add an Interface to the project
	 * @param i interface
	 * @throws StructureException if name is not globally unique
	 */
	public void add(Interface i) throws StructureException;
	
	/**
	 * @param name name of Interface
	 * @return the Interface with that name, or null if not exist
	 */
	public Interface getInterfaceByName(String name);
	
	public void replace(Interface oldIn, Interface newIn) throws StructureException;
	
	public void remove(Interface i);

	public void removeAll();

	public Collection<ContainerStructure> getStructures();
	
}