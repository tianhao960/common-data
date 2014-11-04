package com.mars.faith.das.shard;

import java.util.Collection;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * 每个物理分区为一个Shard
 * 
 * @author kriswang
 * 
 */
public interface Shard {

	SqlSessionFactory getSqlSessionFactory();

	SqlSession establishSqlSession();

	/**
	 * @return The ids of the virtual shards that
	 *         are mapped to this physical shard. The returned Set is
	 *         unmodifiable.
	 */
	Set<ShardId> getShardIds();

	Collection<String> getMappedStatementNames();

	boolean hasMapper(Class<?> type);
	
	@Deprecated
	SqlSession getSqlSession();
}
