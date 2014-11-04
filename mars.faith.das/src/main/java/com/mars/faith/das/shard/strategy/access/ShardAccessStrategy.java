package com.mars.faith.das.shard.strategy.access;

import java.util.List;

import com.mars.faith.das.shard.Shard;
import com.mars.faith.das.shard.ShardOperation;
import com.mars.faith.das.shard.strategy.exit.ExitOperationsCollector;
import com.mars.faith.das.shard.strategy.exit.ExitStrategy;

/**
 * 分区访问策略
 * @author kriswang
 *
 */
public interface ShardAccessStrategy {
	<T> T apply(List<Shard> shards, ShardOperation<T> operation, ExitStrategy<T> exitStrategy, ExitOperationsCollector exitOperationsCollector);
}
