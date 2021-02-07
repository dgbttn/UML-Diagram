package com.util;

import java.util.Arrays;

public class StringUtils {
	/**
	 * Sorted array of Java keywords
	 */
	private static String[] javaKeywords = new String[] {
			"abstract",     "assert",       "boolean",      "break",        "byte",
			"case",         "catch",        "char",         "class",        "const",
			"continue",     "default",      "do",           "double",       "else",
			"enum",         "extends",      "final",        "finally",      "float",
			"for",          "goto",         "if",           "implements",   "import",
			"instanceof",   "int",          "interface",    "long",         "native",
			"new",          "package",      "private",      "protected",    "public",
			"return",       "short",        "static",       "strictfp",     "super",
			"switch",       "synchronized", "this",         "throw",        "throws",
			"transient",    "try",          "void",         "volatile",     "while",
	};
	
	private static String[] javaPrimitives = new String[] {
			"boolean", "byte", "char", "double", "float", "int", "long", "short", "String[]"
	};
	
	/**
	 * @param s string to check
	 * @return true if string is a valid Java identifier
	 */
	public static boolean isJavaName(String s) {
		if (s == null || s.isEmpty()) {
	        return false;
	    }
	    if (!Character.isJavaIdentifierStart(s.charAt(0))) {
	        return false;
	    }
//	    for (int i = 1; i < s.length(); i++) {
//	        if (!Character.isJavaIdentifierPart(s.charAt(i))) {
//	            return false;
//	        }
//	    }
	    return !isJavaKeyword(s);
	}
	
	/**
	 * @param s string to check
	 * @return true if string is a valid Java keyword
	 */
	public static boolean isJavaKeyword(String s) {
		return Arrays.binarySearch(javaKeywords, s) >= 0;
	}
	
	/**
	 * @param s string to check
	 * @return true if string is a Java type
	 */
	public static boolean isJavaPrimitive(String s) {
		return true;
		//return Arrays.binarySearch(javaPrimitives, s) >= 0;
	}
	
	public static boolean isJavaType(String s) {
		return isJavaName(s) || isJavaPrimitive(s);
	}
	
	public static boolean isJavaReturnType(String s) {
		return "void".equals(s) || isJavaName(s) || isJavaPrimitive(s);
	}
}
