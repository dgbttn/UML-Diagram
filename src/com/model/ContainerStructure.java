package com.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Super class for Class and Interface 
 */
public abstract class ContainerStructure extends Structure {
	abstract static class Builder
			<S extends ContainerStructure, B extends Builder<S, B>>
			extends Structure.Builder<S, B> {
		Builder() { super(); }
		
		public B withProperty(Property p) {
			structure.properties.add(p);
			return builder;
		}
		
		public B withProperties(Set<Property> properties) {
			for (Property p : properties) {
				structure.properties.add(p);
			}
			return builder;
		}
		
		public B withMethod(Method m) {
			structure.methods.add(m);
			return builder;
		}
		
		public B withMethods(Set<Method> methods) {
			for (Method m : methods) {
				structure.methods.add(m);
			}
			return builder;
		}
	}

	Set<Property> properties = new LinkedHashSet<>();
	Set<Method> methods = new LinkedHashSet<>();
	
	public Set<Property> getProperties() {
		return Collections.unmodifiableSet(properties);
	}
	
	public Set<Method> getMethods() {
		return Collections.unmodifiableSet(methods);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof ContainerStructure))
			return false;
		return ((ContainerStructure)o).name.equals(this.name);
	}
}