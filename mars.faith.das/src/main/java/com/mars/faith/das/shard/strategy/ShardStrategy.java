package com.mars.faith.das.shard.strategy;

import com.mars.faith.das.shard.strategy.access.ShardAccessStrategy;
import com.mars.faith.das.shard.strategy.reduce.ShardReduceStrategy;
import com.mars.faith.das.shard.strategy.resolution.ShardResolutionStrategy;
import com.mars.faith.das.shard.strategy.selection.ShardSelectionStrategy;

/**
 * 
 * @author kriswang
 *
 */
public interface ShardStrategy {
	
	ShardSelectionStrategy getShardSelectionStrategy();

	ShardResolutionStrategy getShardResolutionStrategy();

	ShardAccessStrategy getShardAccessStrategy();

	ShardReduceStrategy getShardReduceStrategy();
}
