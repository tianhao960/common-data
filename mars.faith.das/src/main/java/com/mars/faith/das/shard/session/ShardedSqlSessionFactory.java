package com.mars.faith.das.shard.session;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.session.SqlSessionFactory;

import com.mars.faith.das.shard.ShardId;
import com.mars.faith.das.shard.id.IdGenerator;
import com.mars.faith.das.shard.strategy.ShardStrategyFactory;

/**
 * 
 * @author kriswang
 * 
 */
public interface ShardedSqlSessionFactory extends SqlSessionFactory {

	List<SqlSessionFactory> getSqlSessionFactories();

	ShardedSqlSessionFactory getSqlSessionFactory(List<ShardId> shardIds, ShardStrategyFactory shardStrategyFactory);

	ShardedSqlSession openSession();

	IdGenerator getIdGenerator();

	Map<SqlSessionFactory, Set<ShardId>> getSqlSessionFactoryShardIdMap();
}
