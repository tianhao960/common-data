package com.mars.faith.das.shard.strategy.exit.impl;

import java.util.List;

import org.apache.ibatis.exceptions.TooManyResultsException;

import com.mars.faith.das.shard.Shard;
import com.mars.faith.das.shard.strategy.exit.ExitOperationsCollector;
import com.mars.faith.das.shard.strategy.exit.ExitStrategy;
import com.mars.faith.das.shard.util.Lists;

/**
 * 
 * @author kriswang
 *
 */
public class SelectOneExitStrategy implements ExitStrategy<Object> {

	private final List<Object> nonNullResult = Lists.newArrayList();

	public synchronized boolean addResult(Object result, Shard shard) {
		if(result != null){
			nonNullResult.add(result);
		}
		return false;
	}

	public Object compileResults(ExitOperationsCollector exitOperationsCollector) {
		List<Object> list = exitOperationsCollector.apply(nonNullResult);

		if (list.size() == 1) {
			return list.get(0);
		} else if (list.size() > 1) {
			throw new TooManyResultsException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
		} else {
			return null;
		}
	}

}
