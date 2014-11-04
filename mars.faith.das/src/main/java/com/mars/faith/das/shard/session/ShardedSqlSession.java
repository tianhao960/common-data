package com.mars.faith.das.shard.session;

import org.apache.ibatis.session.SqlSession;

import com.mars.faith.das.shard.ShardId;

/**
 * 
 * @author kriswang
 *
 */
public interface ShardedSqlSession extends SqlSession {

	SqlSession getSqlSessionForStatement(String statement);

	ShardId getShardIdForStatementOrParameter(String statement, Object parameter);
}
