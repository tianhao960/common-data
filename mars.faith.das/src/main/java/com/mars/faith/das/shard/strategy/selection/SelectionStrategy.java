package com.mars.faith.das.shard.strategy.selection;

import com.mars.faith.das.shard.ShardId;

/**
 * To create new sharding strategy for new sharding object
 * @author kriswang
 *
 * @param <T>
 */
public interface SelectionStrategy<T> {
	ShardId selectShardIdForNewObject(T entity);
}
