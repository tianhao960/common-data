package com.mars.faith.das.shard.session.impl;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.springframework.util.Assert;

import com.mars.faith.das.shard.ShardId;
import com.mars.faith.das.shard.cfg.MyBatisConfigurationsWrapper;
import com.mars.faith.das.shard.id.IdGenerator;
import com.mars.faith.das.shard.session.ShardedSqlSession;
import com.mars.faith.das.shard.session.ShardedSqlSessionFactory;
import com.mars.faith.das.shard.strategy.ShardStrategy;
import com.mars.faith.das.shard.strategy.ShardStrategyFactory;
import com.mars.faith.das.shard.util.Iterables;
import com.mars.faith.das.shard.util.Lists;
import com.mars.faith.das.shard.util.Maps;
import com.mars.faith.das.shard.util.Sets;

/**
 * 
 * @author kriswang
 *
 */
public class ShardedSqlSessionFactoryImpl implements ShardedSqlSessionFactory {

	private final Log log = LogFactory.getLog(getClass());

	// the id of the control shard
	private static final int CONTROL_SHARD_ID = 0;

	private final List<SqlSessionFactory> sqlSessionFactories;
	private final List<ShardId> shardIds;

	// map of SessionFactories used by this ShardedSessionFactory (might be a subset of all SessionFactories)
	private final Map<SqlSessionFactory, Set<ShardId>> sqlSessionFactoryShardIdMap;

	// map of all existing SessionFactories, used when creating a new ShardedSessionFactory for some subset of shards
	@SuppressWarnings("unused")
	private final Map<SqlSessionFactory, Set<ShardId>> fullSqlSessionFactoryShardIdMap;

	private final ShardStrategy shardStrategy;

	private final IdGenerator idGenerator;

	// Reference to the SessionFactory we use for functionality that expects
	// data to live in a single, well-known location (like distributed sequences)
	private final SqlSessionFactory controlSqlSessionFactory;

	private final ShardedSqlSession singleShardedSqlSession;

	private final Configuration configurationsWrapper;

	public ShardedSqlSessionFactoryImpl(
			Map<SqlSessionFactory, Set<ShardId>> sessionFactoryShardIdMap,
			ShardStrategyFactory shardStrategyFactory, IdGenerator idGenerator) {

		this.sqlSessionFactories = Lists.newArrayList(sessionFactoryShardIdMap.keySet());
		this.sqlSessionFactoryShardIdMap = Maps.newHashMap();
		this.fullSqlSessionFactoryShardIdMap = sessionFactoryShardIdMap;
		this.shardIds = Lists.newArrayList(Iterables.concat(sessionFactoryShardIdMap.values()));

		Set<ShardId> uniqueShardIds = Sets.newHashSet();
		SqlSessionFactory controlSqlSessionFactoryToSet = null;
		for (Map.Entry<SqlSessionFactory, Set<ShardId>> entry : sessionFactoryShardIdMap.entrySet()) {
			SqlSessionFactory implementor = entry.getKey();
			Assert.notNull(implementor);

			Set<ShardId> shardIdSet = entry.getValue();
			Assert.notNull(shardIdSet);
			Assert.notNull(!shardIdSet.isEmpty());
			for (ShardId shardId : shardIdSet) {
				// TODO(tomislav): we should change it so we specify control shard in configuration
		        if (shardId.getId() == CONTROL_SHARD_ID) {
		        	controlSqlSessionFactoryToSet = implementor;
		        }
		        if(!uniqueShardIds.add(shardId)) {
		        	final String msg = String.format("Cannot have more than one shard with shard id %d.", shardId.getId());
		        	log.error(msg);
		        	throw new RuntimeException(msg);
		        }
		        if (shardIds.contains(shardId)) {
		        	if (!this.sqlSessionFactoryShardIdMap.containsKey(implementor)) {
		        		this.sqlSessionFactoryShardIdMap.put(implementor, Sets.<ShardId>newHashSet());
		        	}
		        	this.sqlSessionFactoryShardIdMap.get(implementor).add(shardId);
		        }
			}
	    }

		this.controlSqlSessionFactory = controlSqlSessionFactoryToSet;

		this.shardStrategy = shardStrategyFactory.newShardStrategy(shardIds);

		this.idGenerator = idGenerator;

		this.singleShardedSqlSession = new ShardedSqlSessionImpl(this, shardStrategy);

		this.configurationsWrapper = new MyBatisConfigurationsWrapper(getAnyFactory().getConfiguration(), this.getSqlSessionFactories());

	}

	private SqlSessionFactory getAnyFactory() {
		return sqlSessionFactories.get(0);
	}

	
	public List<SqlSessionFactory> getSqlSessionFactories() {
		return Collections.<SqlSessionFactory> unmodifiableList(sqlSessionFactories);
	}

	
	public ShardedSqlSessionFactory getSqlSessionFactory(
			List<ShardId> shardIds, ShardStrategyFactory shardStrategyFactory) {
		throw new UnsupportedOperationException();
	}

	public SqlSession openControlSession() {
		Assert.notNull(controlSqlSessionFactory);

		SqlSession session = controlSqlSessionFactory.openSession();
	    return  session;
	}

	
	public ShardedSqlSession openSession() {
		return this.openSession(false);
	}

	
	public ShardedSqlSession openSession(boolean autoCommit) {
		return this.openSession(ExecutorType.SIMPLE, autoCommit);
	}

	
	public ShardedSqlSession openSession(ExecutorType execType) {
		return this.openSession(execType, false);
	}

	
	public ShardedSqlSession openSession(ExecutorType execType, boolean autoCommit) {
		return singleShardedSqlSession;
	}

	
	public ShardedSqlSession openSession(TransactionIsolationLevel level) {
		return this.openSession(ExecutorType.SIMPLE, level);
	}

	
	public ShardedSqlSession openSession(ExecutorType execType,
			TransactionIsolationLevel level) {
		return singleShardedSqlSession;
	}

	
	public ShardedSqlSession openSession(Connection connection) {
		throw new UnsupportedOperationException(
				"Cannot open a sharded session with a user provided connection.");
	}

	
	public ShardedSqlSession openSession(ExecutorType execType,
			Connection connection) {
		throw new UnsupportedOperationException(
				"Cannot open a sharded session with a user provided connection.");
	}

	
	public Configuration getConfiguration() {
		return configurationsWrapper;
	}

	
	public IdGenerator getIdGenerator() {
		return idGenerator;
	}

	public Map<SqlSessionFactory, Set<ShardId>> getSqlSessionFactoryShardIdMap() {
		return sqlSessionFactoryShardIdMap;
	}

}
