package com.mars.faith.das.shard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.Assert;

/**
 * Base implementation for HasShadIdList. 
 * @author kriswang
 *
 */
public class BaseHasShardIdList implements HasShardIdList {
	
	protected final List<ShardId> shardIds;
	
	protected BaseHasShardIdList(List<ShardId> shardIds) {
		Assert.notNull(shardIds);
		Assert.notNull(!shardIds.isEmpty());
		this.shardIds = new ArrayList<ShardId>(shardIds);
	}

	public List<ShardId> getShardIds() {
		return Collections.unmodifiableList(shardIds);
	}

}
