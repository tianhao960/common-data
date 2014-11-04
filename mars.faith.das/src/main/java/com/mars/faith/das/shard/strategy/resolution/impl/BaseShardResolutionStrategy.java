package com.mars.faith.das.shard.strategy.resolution.impl;

import java.util.List;

import com.mars.faith.das.shard.BaseHasShardIdList;
import com.mars.faith.das.shard.ShardId;
import com.mars.faith.das.shard.strategy.resolution.ShardResolutionStrategy;

/**
 * 
 * @author kriswang
 *
 */
public abstract class BaseShardResolutionStrategy extends BaseHasShardIdList implements ShardResolutionStrategy {

	protected BaseShardResolutionStrategy(List<ShardId> shardIds) {
		super(shardIds);
	}

}
