package com.mars.faith.das.shard.strategy;

import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import com.mars.faith.das.shard.strategy.access.ShardAccessStrategy;
import com.mars.faith.das.shard.strategy.access.impl.SequentialShardAccessStrategy;
import com.mars.faith.das.shard.strategy.reduce.ShardReduceStrategy;
import com.mars.faith.das.shard.strategy.resolution.ShardResolutionStrategy;
import com.mars.faith.das.shard.strategy.selection.ShardSelectionStrategy;

public class ShardStrategyImpl implements ShardStrategy {

	private ShardSelectionStrategy shardSelectionStrategy;

	private ShardResolutionStrategy shardResolutionStrategy;

	private ShardAccessStrategy shardAccessStrategy;

	private ShardReduceStrategy shardReduceStrategy;

	private final ShardAccessStrategy transactionShardReduceStrategy = new SequentialShardAccessStrategy(); 

	public ShardStrategyImpl(ShardSelectionStrategy shardSelectionStrategy,
			ShardResolutionStrategy shardResolutionStrategy,
			ShardAccessStrategy shardAccessStrategy, ShardReduceStrategy shardReduceStrategy) {

		Assert.notNull(shardSelectionStrategy);
		Assert.notNull(shardResolutionStrategy);
		Assert.notNull(shardAccessStrategy);
		Assert.notNull(shardReduceStrategy);

		this.shardSelectionStrategy = shardSelectionStrategy;
		this.shardResolutionStrategy = shardResolutionStrategy;
		this.shardAccessStrategy = shardAccessStrategy;
		this.shardReduceStrategy = shardReduceStrategy;
	}

	
	public ShardSelectionStrategy getShardSelectionStrategy() {
		return shardSelectionStrategy;
	}
	
	public ShardResolutionStrategy getShardResolutionStrategy() {
		return shardResolutionStrategy;
	}

	public ShardAccessStrategy getShardAccessStrategy() {

		return TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? shardAccessStrategy : transactionShardReduceStrategy;
	}
	
	public ShardReduceStrategy getShardReduceStrategy() {
		return shardReduceStrategy;
	}

}
