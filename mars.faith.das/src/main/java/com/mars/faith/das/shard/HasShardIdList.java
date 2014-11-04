package com.mars.faith.das.shard;

import java.util.List;

/**
 * Interface for objects that can provide a List of ShardIds.
 * @author kriswang
 *
 */
public interface HasShardIdList {
	
	List<ShardId> getShardIds();
}
