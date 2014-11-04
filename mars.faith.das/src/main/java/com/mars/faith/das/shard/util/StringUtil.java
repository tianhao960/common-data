package com.mars.faith.das.shard.util;

/**
 * String utility
 * 
 * @author kriswang
 * 
 */
public class StringUtil {

	public static boolean isEmptyOrWhitespace(String s) {
		s = makeSafe(s);
		
		for(int i = 0; i < s.length(); i++) {
			if(!Character.isWhitespace(s.charAt(i))) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Helper function for making null strings safe for comparisons, etc.
	 * 
	 * @return (s == null) ? "" : s;
	 */
	public static String makeSafe(String s) {
		return (s == null) ? "" : s;
	}
	
	/**
	 * @return true if s == null or s.equals("")
	 */
	public static boolean isEmpty(String s) {
		return makeSafe(s).length() == 0;
	}
	
	public static String capitalize(String s) {
		if(s.length() == 0) {
			return s;
		} 
		
		char first = s.charAt(0);
		char capitalized = Character.toUpperCase(first);
		
		return (first == capitalized) ? s : capitalized + s.substring(1);
	}
}
