package com.mars.faith.das.shard.strategy.access.impl;

import java.util.List;

import lombok.extern.apachecommons.CommonsLog;

import com.mars.faith.das.shard.Shard;
import com.mars.faith.das.shard.ShardId;
import com.mars.faith.das.shard.ShardOperation;
import com.mars.faith.das.shard.strategy.access.ShardAccessStrategy;
import com.mars.faith.das.shard.strategy.exit.ExitOperationsCollector;
import com.mars.faith.das.shard.strategy.exit.ExitStrategy;

/**
 * Access following sequential order.
 * 
 * @author kriswang
 * 
 */
@CommonsLog
public class SequentialShardAccessStrategy implements ShardAccessStrategy {

	public <T> T apply(List<Shard> shards, ShardOperation<T> operation, ExitStrategy<T> exitStrategy,
			ExitOperationsCollector exitOperationsCollector) {

		execute: for (Shard shard : shards) {
			for (ShardId shardId : shard.getShardIds()) {
				if (exitStrategy.addResult(operation.execute(shard.establishSqlSession(), shardId), shard)) {
					log.debug(String.format("Short-circuiting operation %s after execution against shard %s",
							operation.getOperationName(), shard));
					break execute;
				}
			}
		}

		return exitStrategy.compileResults(exitOperationsCollector);
	}

	/**
	 * Override this method if you want to control the order in which the shards
	 * are operated on (this comes in handy when paired with exit strategies
	 * that allow early exit because it allows you to evenly distribute load).
	 * Deafult implementation is to just iterate in the same order every time.
	 * 
	 * @param shards
	 *            The shards we might want to reorder
	 * @return Reordered view of the shards.
	 */
	protected Iterable<Shard> getNextOrderingOfShards(List<Shard> shards) {
		return shards;
	}
}
