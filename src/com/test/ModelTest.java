package com.test;

import com.model.*;
import com.model.Class;

public class ModelTest {
	public static void main(String[] args) throws StructureException {
		Project p = Project.getInstance();
		Class.Builder cb = p.getClassBuilder();
		Interface.Builder ib = p.getInterfaceBuilder();
		Property.Builder fb = p.getPropertyBuilder();
		Method.Builder mb = p.getMethodBuilder();
		
		Method m = mb.withName("amethod")
					 .isAbstract(true)
					 .withArgument(new Argument("x", "int"))
					 .withReturnType("String")
					 .withVisibility(Structure.Visibility.PUBLIC)
					 .build();
		Interface i = ib.withName("AInterface")
						.withMethod(m)
						.build();
		p.add(i);
		
		Class c1 = cb.withName("AClass1")
					 .isAbstract(true)
					 .implement(i)
					 .build();
		Class c2 = cb.withName("AClass2")
					 .extend(c1)
					 .build();
		
		p.add(c1);
		p.add(c2);
	}
}
