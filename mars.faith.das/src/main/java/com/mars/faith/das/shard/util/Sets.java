package com.mars.faith.das.shard.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper methods related to {@link Set}s.
 * 
 * @author kriswang
 * 
 */
public class Sets {
	private Sets() {
	}

	/**
	 * Construct a new {@link HashSet}, taking advantage of type inference to
	 * avoid specifying the type on the rhs.
	 */
	public static <E> HashSet<E> newHashSet() {
		return new HashSet<E>();
	}

	/**
	 * Construct a new {@link HashSet} with the provided elements, taking
	 * advantage of type inference to avoid specifying the type on the rhs.
	 */
	public static <E> HashSet<E> newHashSet(E... elements) {
		HashSet<E> set = newHashSet();
		Collections.addAll(set, elements);
		return set;
	}

	/**
	 * Construct a new {@link HashSet} with the contents of the provided
	 * {@link Iterable}, taking advantage of type inference to avoid specifying
	 * the type on the rhs.
	 */
	public static <E> HashSet<E> newHashSet(Iterable<? extends E> elements) {
		HashSet<E> set = newHashSet();
		for (E e : elements) {
			set.add(e);
		}
		return set;
	}
}
