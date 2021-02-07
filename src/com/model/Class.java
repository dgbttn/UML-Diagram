package com.model;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class representation of a class
 */
public class Class extends ContainerStructure {
	public static class Builder
			extends ContainerStructure.Builder<Class, Builder> {

		Builder() { super(); }
		
		@Override
		Class createStructure() { return new Class(); }

		@Override
		Builder getThis() { return this; }
		
		public Builder isAbstract(boolean flag) {
			structure.isAbstract = flag; 
			return builder;
		}
		
		public Builder extend(Class c) {
			structure.superClass = c;
			return builder;
		}
		
		public Builder implement(Interface i) {
			structure.interfaces.add(i);
			return builder;
		}
		
		public Builder implement(Set<Interface> interfaces) {
			for (Interface i : interfaces)
				structure.interfaces.add(i);
			return builder;
		}
		
		@Override
		public void validate() throws StructureException {
			super.validate();
			if (!structure.isAbstract()) {
				for (Method m : structure.methods) {
					if (m.isAbstract())
						throw new IllegalModifierException();

				}
			}
		}
	}
	
	
	private boolean isAbstract;
	private Class superClass;
	private Set<Class> subClasses;
	private Set<Interface> interfaces;

	private Class() {
		isAbstract = false;
		superClass = null;
		subClasses = new HashSet<>();
		interfaces = new HashSet<>();
	}
	
	public boolean isAbstract() { return isAbstract; }
	
	public Class getSuperClass() { return superClass; }
	
	public Set<Class> getSubClasses() { 
		return Collections.unmodifiableSet(subClasses); 
	}
	
	public Set<Interface> getInterfaces() { 
		return Collections.unmodifiableSet(interfaces); 
	}
	
	@Override
	public final boolean equals(Object o) {
		return super.equals(o);
	}
	
	boolean addSubClass(Class c) { return subClasses.add(c); }
	
	boolean removeSubClass(Class c) { return subClasses.remove(c); }
	
	void addSuperClass(Class c) { superClass = c; }
	
	void removeSuperClass() { superClass = null; }
	
	boolean removeInterface(Interface i) { return interfaces.remove(i); }
	
	void invalidate() {
		superClass = null;
		subClasses = null;
		interfaces = null;
		properties = null;
		methods = null;
	}

	@Override
	public String toString() {
		return this.getName();
	}
}