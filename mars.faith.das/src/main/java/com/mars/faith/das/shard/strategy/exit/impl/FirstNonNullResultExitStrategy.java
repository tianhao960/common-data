package com.mars.faith.das.shard.strategy.exit.impl;

import org.springframework.util.Assert;

import com.mars.faith.das.shard.Shard;
import com.mars.faith.das.shard.strategy.exit.ExitOperationsCollector;
import com.mars.faith.das.shard.strategy.exit.ExitStrategy;

/**
 * 
 * @author kriswang
 * 
 * @param <T>
 */
public class FirstNonNullResultExitStrategy<T> implements ExitStrategy<T> {

	private T nonNullResult;
	private Shard shard;

	/**
	 * Synchronized method guarantees that only the first thread to add a result
	 * will have its result reflected.
	 */
	public final synchronized boolean addResult(T result, Shard shard) {
		Assert.notNull(shard);
		if (result != null && nonNullResult == null) {
			nonNullResult = result;
			this.shard = shard;
			return true;
		}
		return false;
	}

	public T compileResults(ExitOperationsCollector exitOperationsCollector) {
		return nonNullResult;
	}

	public Shard getShardOfResult() {
		return shard;
	}

}
