package com.mars.faith.das.shard.strategy.resolution.impl;

import java.util.List;

import com.mars.faith.das.shard.ShardId;
import com.mars.faith.das.shard.strategy.resolution.ShardResolutionStrategyData;

/**
 * 
 * @author kriswang
 *
 */
public class AllShardsShardResolutionStrategy extends BaseShardResolutionStrategy {

	protected AllShardsShardResolutionStrategy(List<ShardId> shardIds) {
		super(shardIds);
	}

	public List<ShardId> selectShardIdsFromShardResolutionStrategyData(
			ShardResolutionStrategyData shardResolutionStrategyData) {

		return super.getShardIds();
	}

}
