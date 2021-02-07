package com.model;

import com.util.StringUtils;

public class Property extends Structure {
	public static class Builder
			extends Structure.Builder<Property, Builder> {

		@Override
		Property createStructure() { return new Property(); }

		@Override
		Builder getThis() { return this; }
		
		public Builder withVisibility(Visibility v) {
			structure.visibility = v;
			return builder;
		}
		
		public Builder isStatic(boolean flag) {
			structure.isStatic = flag;
			return builder;
		}
		
		public Builder withType(String type) {
			structure.type = type;
			return builder;
		}
		
		@Override
		public void validate() throws StructureException {
			super.validate();
			if (!StringUtils.isJavaType(structure.type))
				throw new IllegalNameException(structure.type);
		}
	}
	
	private String type;
	
	private Visibility visibility = Visibility.DEFAULT;
	
	private boolean isStatic = false;
	
	private Property() {}
	
	public String getType() {
		return type;
	}
	
	public Visibility getVisibility() {
		return visibility;
	}
	
	public boolean isStatic() {
		return isStatic;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || o.getClass() != this.getClass())
			return false;
		return ((Property)o).name.equals(this.name);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return type+" "+name;
	}
}
