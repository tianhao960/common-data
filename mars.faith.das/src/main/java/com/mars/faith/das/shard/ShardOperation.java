package com.mars.faith.das.shard;

import org.apache.ibatis.session.SqlSession;

/**
 * 
 * @author kriswang
 *
 * @param <T>
 */
public interface ShardOperation<T> {
	
	T execute(SqlSession sqlSession, ShardId shardId);
	
	String getOperationName();

}
