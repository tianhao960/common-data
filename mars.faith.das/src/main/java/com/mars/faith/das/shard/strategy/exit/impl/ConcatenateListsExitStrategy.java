package com.mars.faith.das.shard.strategy.exit.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.mars.faith.das.shard.Shard;
import com.mars.faith.das.shard.strategy.exit.ExitOperationsCollector;
import com.mars.faith.das.shard.strategy.exit.ExitStrategy;

/**
 * Threadsafe ExistStrategy that concatenates all the lists that are added.
 * @author kriswang
 *
 */
public class ConcatenateListsExitStrategy implements ExitStrategy<List<Object>> {
	
	private final List<Object> nonNullResult = new CopyOnWriteArrayList<Object>();

	public boolean addResult(List<Object> result, Shard shard) {
		if(result != null && !result.isEmpty()) {
			nonNullResult.add(result);
		}
		return false;
	}

	public List<Object> compileResults(ExitOperationsCollector exitOperationsCollector) {
		return exitOperationsCollector.apply(this.nonNullResult);
	}

}
