package com.model;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class Interface extends ContainerStructure {
	/**
	 * This class replace the constructor of Interface
	 */
	public static class Builder
			extends ContainerStructure.Builder<Interface, Builder> {

		Builder() { super(); }
		
		@Override
		Interface createStructure() { return new Interface(); }

		@Override
		Builder getThis() { return this; }
		
		public Builder extend(Interface i) {
			structure.superInterfaces.add(i);
			return builder;
		}
		
		public Builder extend(Set<Interface> interfaces) {
			for (Interface i : interfaces)
				structure.superInterfaces.add(i);
			return builder;
		}
		
		@Override
		public void validate() throws StructureException {
			super.validate();
			for (Property f : structure.properties) {
				if (f.getVisibility() != Visibility.PUBLIC || !f.isStatic())
					throw new IllegalModifierException(
							"Interface fields must be PUBLIC STATIC");
			}
			for (Method m : structure.methods) {
				if (!m.isAbstract())
					throw new IllegalModifierException("Interface methods must be ABSTRACT");
			}
		}
	}
	
	private Set<Interface> superInterfaces = new LinkedHashSet<>();
	private Set<Interface> subInterfaces = new LinkedHashSet<>();
	private Set<Class> implementations = new LinkedHashSet<>();
	
	private Interface() {}
	
	boolean addImplementation(Class c) {
		return implementations.add(c);
	}
	
	public Set<Class> getImplementations() { 
		return Collections.unmodifiableSet(implementations); 
	}
	
	boolean removeImplementation(Class c) {
		return implementations.remove(c);
	}
	
	boolean addSuperInterface(Interface i) {
		return superInterfaces.add(i);
	}
	
	public Set<Interface> getSuperInterfaces() { 
		return Collections.unmodifiableSet(superInterfaces); 
	}
	
	boolean removeSuperInterface(Interface i) {
		return superInterfaces.remove(i);
	}
	
	boolean addSubInterface(Interface i) {
		return subInterfaces.add(i);
	}
	
	public Set<Interface> getSubInterfaces() { 
		return Collections.unmodifiableSet(subInterfaces); 
	}
	
	boolean removeSubInterface(Interface i) {
		return subInterfaces.remove(i);
	}
	
	void invalidate() {
		superInterfaces = null;
		subInterfaces = null;
		implementations = null;
		properties = null;
		methods = null;
	}

	@Override
	public String toString() {
		return this.getName();
	}
}
