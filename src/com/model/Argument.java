package com.model;

public class Argument extends Structure{
    private String type;

    private String name;

    public Argument(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getType() { return type; }

    public String getName() { return name; }

    /**
     * 2 argument is equal when they have same name
     * in method context
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass())
            return false;
        return ((Argument)o).name.equals(this.name);
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