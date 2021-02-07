package com.model;

import com.util.StringUtils;

public abstract class Structure {

	abstract static class 
			Builder<S extends Structure, B extends Builder<S, B>> {
		S structure;
		B builder;
		
		Builder() {
			structure = createStructure();
			builder = getThis();
		}
		
		/**
		 * Call S's constructor 
		 * @return a new structure of type S
		 */
		abstract S createStructure();
		
		/**
		 * @return this builder, used for method chaining
		 */
		abstract B getThis();
		
		public B withName(String name) {
			structure.name = name; return builder;
		}
		
		public S build() throws StructureException {
			validate();
			S ret = structure;
			structure = createStructure();
			return ret;
		}
		
		/**
		 * Validate the state of the structure
		 * @throws StructureException
		 */
		void validate() throws StructureException {
			if (!StringUtils.isJavaName(structure.name))
				throw new IllegalNameException(structure.name);
		}
	}
	
	public enum Visibility {
		DEFAULT, PUBLIC, PRIVATE, PROTECTED
	};
	
	String name;
	
	public String getName() { return name; }
}
