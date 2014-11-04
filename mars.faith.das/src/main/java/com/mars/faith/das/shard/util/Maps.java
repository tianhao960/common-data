package com.mars.faith.das.shard.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Helper methods related to {@link Map}s.
 * @author kriswang
 *
 */
public class Maps {
	
	private Maps() {
	}

	/**
	 * Construct a new {@link HashMap}, taking advantage of type inference to
	 * avoid specifying the type on the rhs.
	 */
	public static <K, V> HashMap<K, V> newHashMap() {
		return new HashMap<K, V>();
	}

	/**
	 * Construct a new {@link LinkedHashMap}, taking advantage of type inference
	 * to avoid specifying the type on the rhs.
	 */
	public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
		return new LinkedHashMap<K, V>();
	}
}
