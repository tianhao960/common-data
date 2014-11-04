package com.mars.faith.das.shard.strategy;

import java.util.List;

import com.mars.faith.das.shard.ShardId;

/**
 * 策略工厂
 * @author kriswang
 *
 */
public interface ShardStrategyFactory {
	ShardStrategy newShardStrategy(List<ShardId> shardIds);
}
