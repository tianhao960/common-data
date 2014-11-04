package com.mars.faith.das.shard.strategy.selection;

import com.mars.faith.das.shard.ShardId;

/**
 * 
 * @author kriswang
 * 
 */
public interface ShardSelectionStrategy {
	/**
	 * Determine the specific shard on which this object should reside
	 * 
	 * @param obj
	 *            the new object for which we are selecting a shard
	 * @return the id of the shard on which this object should live
	 */
	ShardId selectShardIdForNewObject(String statement, Object obj);
}
