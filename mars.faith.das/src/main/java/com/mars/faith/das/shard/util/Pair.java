package com.mars.faith.das.shard.util;

/**
 * 
 * @author kriswang
 * 
 * @param <K>
 * @param <V>
 */
public class Pair<K, V> {
	public final K first;

	/** The second element of the pair. */
	public final V second;

	private Pair(/* @Nullable */K first, /* @Nullable */V second) {
		this.first = first;
		this.second = second;
	}

	public K getFirst() {
		return first;
	}

	public V getSecond() {
		return second;
	}

	public static <K, V> Pair<K, V> of(/* @Nullable */K first, /* @Nullable */
			V second) {
		return new Pair<K, V>(first, second);
	}

	private static boolean eq(/* @Nullable */Object a, /* @Nullable */Object b) {
		return a == b || (a != null && a.equals(b));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	@Override
	public boolean equals(/* @Nullable */Object object) {
		if (object instanceof Pair<?, ?>) {
			Pair<?, ?> other = (Pair<?, ?>) object;
			return eq(first, other.first) && eq(second, other.second);
		}
		return false;
	}
}
