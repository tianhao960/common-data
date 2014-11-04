package com.mars.faith.das.shard.session;

import java.util.List;

import com.mars.faith.das.shard.Shard;
import com.mars.faith.das.shard.ShardId;

/**
 * 
 * @author kriswang
 *
 */
public interface ShardIdResolver {
	
	ShardId getShardIdForStatementOrParameter(String statement, Object parameter, List<Shard> shardsToConsider);
	
	ShardId getShardIdForStatementOrParameter(String statement, Object parameter);

}
