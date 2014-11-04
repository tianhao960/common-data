package com.mars.faith.das.shard.strategy.exit;

import com.mars.faith.das.shard.Shard;

/**
 * 
 * @author kriswang
 *
 * @param <T>
 */
public interface ExitStrategy<T> {
	
	/**
	 * Add the provided result and return whether or not the caller can halt
	 * processing.
	 * @param  Result the result to be added
	 * @param  
	 * @return Whether or not the caller can halt processing
	 */
	boolean addResult(T result, Shard shard);
	
	T compileResults(ExitOperationsCollector exitOperationsCollector);
}
