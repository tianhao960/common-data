package com.mars.faith.das.shard.strategy.access.impl;

import java.util.List;
import java.util.Random;

import com.mars.faith.das.shard.Shard;
import com.mars.faith.das.shard.util.Iterables;

/**
 * 
 * @author kriswang
 * 
 */
public class LoadBalancedSequentialShardAccessStrategy extends SequentialShardAccessStrategy {

private final Random rand;

	public LoadBalancedSequentialShardAccessStrategy() {
		this.rand = new Random(System.currentTimeMillis());
	}

	protected Iterable<Shard> getNextOrderingOfShards(List<Shard> shards) { 
		return Iterables.rotate(shards, rand.nextInt() % shards.size());
	}
}
