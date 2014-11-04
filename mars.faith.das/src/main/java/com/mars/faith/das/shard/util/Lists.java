package com.mars.faith.das.shard.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper methods related to {@link List}s.
 * 
 * @author kriswang
 * 
 */
public class Lists {

	private Lists() {

	}

	/**
	 * Construct a new {@link ArrayList}, taking advantage of type inference to
	 * avoid specifying the type on the rhs.
	 */
	public static <E> ArrayList<E> newArrayList() {
		return new ArrayList<E>();
	}

	/**
	 * Construct a new {@link ArrayList} with the specified capacity, taking
	 * advantage of type inference to avoid specifying the type on the rhs.
	 * 
	 * @param initialCapacity
	 * @return
	 */
	public static <E> ArrayList<E> newArrayListWithCapacity(int initialCapacity) {
		return new ArrayList<E>(initialCapacity);
	}

	public static <E> ArrayList<E> newArrayList(E... elements) {
		ArrayList<E> set = newArrayList();
		Collections.addAll(set, elements);
		return set;
	}
	
	/**
	 * Construct a new {@link ArrayList} with the contents of the provided
	 * {@link Iterable}, taking advantage of type inference to avoid specifying
	 * the type on the rhs.
	 * 
	 * @param elements
	 * @return
	 */
	public static <E> ArrayList<E> newArrayList(Iterable<? extends E> elements) {
		ArrayList<E> list = new ArrayList<E>();
		for (E e : elements) {
			list.add(e);
		}
		return list;
	}

	/**
	 * Construct a new {@link LinkedList}, taking advantage of type inference to
	 * avoid specifying the type on the rhs.
	 */
	public static <E> LinkedList<E> newLinkedList() {
		return new LinkedList<E>();
	}
	
	/**
	 * list intersection
	 * @param a
	 * @param b
	 * @return
	 */
	public static <E> List<E> intersection(List<E> a, List<E> b) {
		List<E> dest = newArrayList(a);
		dest.retainAll(b);
		return dest;
	}
	
	/**
	 * list union
	 * @param a
	 * @param b
	 * @return
	 */
	public static <E> List<E> union(List<E> a, List<E> b) {
		List<E> dest = newArrayList(a);
		
		for(E e : b) {
			if(!dest.contains(e)) {
				dest.add(e);
			}
		}
		
		return dest;
	}
	
	/**
	 * substraction(差集)
	 * @param a
	 * @param b
	 * @return
	 */
	public static <E> List<E> substraction(List<E> a, List<E> b) {
		List<E> dest = union(a, b);
		dest.removeAll(intersection(a,b));
		return dest;
	}
	
	/**
	 * 补集
	 * @param a
	 * @param b
	 * @return
	 */
	public static<E> List<E> complement(List<E> a, List<E> b) {
		List<E> dest = union(a, b);
		dest.removeAll(b);
		return dest;
	}
}
