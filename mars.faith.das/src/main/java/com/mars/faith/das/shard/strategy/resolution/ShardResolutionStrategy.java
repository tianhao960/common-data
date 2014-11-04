package com.mars.faith.das.shard.strategy.resolution;

import java.util.List;

import com.mars.faith.das.shard.ShardId;

/**
 * 分区抉择策略 
 * @author kriswang
 *
 */
public interface ShardResolutionStrategy {
	/**
	 * 决定此查询在哪些分区上执行
	 *
	 * @param shardResolutionStrategyData information we can use to select shards
	 * @return the ids of the shards on which the object described by the ShardSelectionStrategyData might reside
	 */
	List<ShardId> selectShardIdsFromShardResolutionStrategyData(ShardResolutionStrategyData shardResolutionStrategyData);
}
