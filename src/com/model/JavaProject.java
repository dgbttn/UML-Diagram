package com.model;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implement Project
 * with Java specification
 */
public class JavaProject implements Project {
	private Map<String, ContainerStructure> nameMap = new LinkedHashMap<>();

	JavaProject() {}

	@Override
	public Class.Builder getClassBuilder() {
		return new Class.Builder();
	}

	@Override
	public Interface.Builder getInterfaceBuilder() {
		return new Interface.Builder();
	}

	@Override
	public Method.Builder getMethodBuilder() {
		return new Method.Builder();
	}

	@Override
	public Property.Builder getPropertyBuilder() {
		return new Property.Builder();
	}
	
	@Override
	public void add(Class c) throws StructureException {
		validate(c);
		Class superClass = c.getSuperClass();
		if (superClass != null) superClass.addSubClass(c);
		for (Interface i : c.getInterfaces())
			i.addImplementation(c);
		nameMap.put(c.name, c);
	}

	@Override
	public Class getClassByName(String name) {
		ContainerStructure c = nameMap.get(name);
		if (c == null || !(c instanceof Class)) return null;
		return (Class)c;
	}

	@Override
	public void replace(Class oldCl, Class newCl) throws StructureException {
		if (!oldCl.name.equals(newCl.name))
			validate(newCl);
		Class superClass = oldCl.getSuperClass();
		if (superClass != null) superClass.removeSubClass(oldCl);
		for (Class c : oldCl.getSubClasses())
			c.addSuperClass(newCl);
		for (Interface i : oldCl.getInterfaces())
			i.removeImplementation(oldCl);
		nameMap.remove(oldCl.name);
		oldCl.invalidate();
		add(newCl);
	}

	@Override
	public void remove(Class c) {
		c.getSuperClass().removeSubClass(c);
		for (Class cl : c.getSubClasses())
			cl.removeSuperClass();
		for (Interface i : c.getInterfaces())
			i.removeImplementation(c);
		nameMap.remove(c.name);
		c.invalidate();
	}

	@Override
	public void add(Interface i) throws StructureException {
		validate(i);
		for (Interface in : i.getSuperInterfaces())
			in.addSubInterface(i);
		nameMap.put(i.name, i);
	}

	@Override
	public Interface getInterfaceByName(String name) {
		ContainerStructure c = nameMap.get(name);
		if (c == null || !(c instanceof Interface)) return null;
		return (Interface)c;
	}

	@Override
	public void replace(Interface oldIn, Interface newIn) throws StructureException {
		if (!oldIn.name.equals(newIn.name))
			validate(newIn);
		for (Interface in : oldIn.getSuperInterfaces())
			in.removeSubInterface(oldIn);
		for (Interface in : oldIn.getSubInterfaces()) { // newIn will have oldIn's subInterfaces
			in.removeSuperInterface(oldIn);
			in.addSuperInterface(newIn);
		}
		for (Class c : oldIn.getImplementations())
			c.removeInterface(oldIn);
		
		nameMap.remove(oldIn.name);
		oldIn.invalidate();
		add(newIn);
	}

	@Override
	public void remove(Interface i) {
		for (Interface in : i.getSuperInterfaces())
			in.removeSubInterface(i);
		for (Interface in : i.getSubInterfaces())
			in.removeSuperInterface(i);
		for (Class c : i.getImplementations())
			c.removeInterface(i);
		nameMap.remove(i.name);
		i.invalidate();
	}

	@Override
	public void removeAll() {
		nameMap.clear();
	}

	private void validate(ContainerStructure c) throws StructureException {
		if (nameMap.containsKey(c.name))
			throw new DuplicateStructureException();
	}

	@Override
	public Collection<ContainerStructure>  getStructures() {
		return Collections.unmodifiableCollection(nameMap.values());
	}
}
