package com.mars.faith.das.shard.strategy.access.impl;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import lombok.extern.apachecommons.CommonsLog;

import org.springframework.util.Assert;

import com.mars.faith.das.shard.Shard;
import com.mars.faith.das.shard.ShardId;
import com.mars.faith.das.shard.ShardOperation;
import com.mars.faith.das.shard.strategy.access.ShardAccessStrategy;
import com.mars.faith.das.shard.strategy.exit.ExitOperationsCollector;
import com.mars.faith.das.shard.strategy.exit.ExitStrategy;
import com.mars.faith.das.shard.util.Lists;

/**
 * parallel access strategy
 * 
 * @author kriswang
 * 
 */
@CommonsLog
public class ParallelShardAccessStrategy implements ShardAccessStrategy {

	private final ThreadPoolExecutor executor;

	public ParallelShardAccessStrategy(ThreadPoolExecutor executor) {
		Assert.notNull(executor);
		this.executor = executor;
	}

	public <T> T apply(List<Shard> shards, ShardOperation<T> operation, ExitStrategy<T> exitStrategy,
			ExitOperationsCollector exitOperationsCollector) {
		List<StartAwareFutureTask> tasks = Lists.newArrayListWithCapacity(shards.size());

		int taskId = 0;

		CountDownLatch startSignal = new CountDownLatch(1);
		CountDownLatch doneSignal = new CountDownLatch(shards.size());

		for (Shard shard : shards) {
			// create task for each shard
			for (final ShardId shardId : shard.getShardIds()) {
				ParallelShardOperationCallable<T> callable = new ParallelShardOperationCallable<T>(startSignal,
						doneSignal, exitStrategy, operation, shard, shardId, tasks);
				// wrap the task in a StartAwareFutureTask so that the task can be cancelled
				StartAwareFutureTask ft = new StartAwareFutureTask(callable, taskId++);
				tasks.add(ft);
				// execute task
				executor.execute(ft);
			}
		}
		
		startSignal.countDown();
		
		try{
			log.debug("Waiting for threads to complete processing before proceeding.");
			doneSignal.await();
		} catch (InterruptedException e) {
			// not sure why this would happen or what we should do if it does
			log.error("Received unexpected exception while waiting for done signal.", e);
		}

		log.debug("Compiling results.");
		return exitStrategy.compileResults(exitOperationsCollector);
	}

}
