package com.mars.faith.das.shard.session.impl;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.util.Assert;

import com.mars.faith.das.shard.DefaultShard;
import com.mars.faith.das.shard.Shard;
import com.mars.faith.das.shard.ShardId;
import com.mars.faith.das.shard.ShardOperation;
import com.mars.faith.das.shard.id.IdGenerator;
import com.mars.faith.das.shard.select.impl.AdHocSelectFactoryImpl;
import com.mars.faith.das.shard.select.impl.ShardSelectImpl;
import com.mars.faith.das.shard.session.ShardIdResolver;
import com.mars.faith.das.shard.session.ShardedSqlSession;
import com.mars.faith.das.shard.session.ShardedSqlSessionFactory;
import com.mars.faith.das.shard.strategy.ShardStrategy;
import com.mars.faith.das.shard.strategy.exit.impl.ExitOperationsSelectCollector;
import com.mars.faith.das.shard.strategy.exit.impl.FirstNonNullResultExitStrategy;
import com.mars.faith.das.shard.strategy.resolution.ShardResolutionStrategyData;
import com.mars.faith.das.shard.strategy.resolution.impl.ShardResolutionStrategyDataImpl;
import com.mars.faith.das.shard.util.Lists;
import com.mars.faith.das.shard.util.Maps;
import com.mars.faith.das.shard.util.ParameterUtil;
import com.mars.faith.das.shard.util.Sets;

/**
 * 
 * @author kriswang
 *
 */
public class ShardedSqlSessionImpl implements ShardedSqlSession, ShardIdResolver {
	
	private final Log log = LogFactory.getLog(getClass());

	private static ThreadLocal<ShardId> currentSubgraphShardId = new ThreadLocal<ShardId>();

	private final ShardedSqlSessionFactory shardedSqlSessionFactory;

	private final List<Shard> shards;

	private final Map<ShardId, Shard> shardIdsToShards;

	private final ShardStrategy shardStrategy;

	public ShardedSqlSessionImpl(ShardedSqlSessionFactory shardedSqlSessionFactory,
			ShardStrategy shardStrategy) {
		this.shardedSqlSessionFactory = shardedSqlSessionFactory;
		this.shards = buildShardListFromSqlSessionFactoryShardIdMap(
				shardedSqlSessionFactory.getSqlSessionFactoryShardIdMap(), this);
		this.shardIdsToShards = buildShardIdsToShardsMap();
		this.shardStrategy = shardStrategy;
	}

	static List<Shard> buildShardListFromSqlSessionFactoryShardIdMap(
			Map<SqlSessionFactory, Set<ShardId>> sqlSessionFactoryShardIdMap,
			ShardIdResolver shardIdResolver) {
		List<Shard> list = Lists.newArrayList();
		for (Map.Entry<SqlSessionFactory, Set<ShardId>> entry : sqlSessionFactoryShardIdMap
				.entrySet()) {
			Shard shard = new DefaultShard(entry.getValue(), entry.getKey());
			list.add(shard);

		}

		return list;
	}

	private Map<ShardId, Shard> buildShardIdsToShardsMap() {
		Map<ShardId, Shard> map = Maps.newHashMap();
		for (Shard shard : shards) {
			for (ShardId shardId : shard.getShardIds()) {
				map.put(shardId, shard);
			}
		}
		return map;
	}

	/**
	 * 
	 */
	private Shard getShardForStatement(String statement, List<Shard> shardsToConsider) {
		//TODO此处可做本地缓存
		// 首先查找主分区？如果没有再找其他分区？
		for (Shard shard : shardsToConsider) {
			if (shard.getSqlSessionFactory() != null
					&& shard.getMappedStatementNames().contains(statement)) {
				return shard;
			}
		}
		return null;
	}

	private SqlSession getSqlSessionForStatement(String statement, List<Shard> shardsToConsider) {
		Shard shard = getShardForStatement(statement, shardsToConsider);
		if (shard == null) {
			return null;
		}
		return shard.establishSqlSession();
	}

	/**
	 * 将虚拟分区转化为物理分区
	 */
	private List<Shard> shardIdListToShardList(List<ShardId> shardIds) {
		Set<Shard> shards = Sets.newHashSet();
		for (ShardId shardId : shardIds) {
			shards.add(shardIdsToShards.get(shardId));
		}
		return Lists.newArrayList(shards);
	}

	/**
	 * @return 所有物理分区
	 */
	public List<Shard> getShards() {
		return Collections.unmodifiableList(shards);
	}

	
	public SqlSession getSqlSessionForStatement(String statement) {
		return getSqlSessionForStatement(statement, shards);
	}

	
	public ShardId getShardIdForStatementOrParameter(String statement, Object parameter) {
		return getShardIdForStatementOrParameter(statement, parameter, shards);
	}

	
	public ShardId getShardIdForStatementOrParameter(String statement, Object parameter,
			List<Shard> shardsToConsider) {
		// TODO(maxr) optimize this by keeping an identity map of objects to shardId
		Shard shard = getShardForStatement(statement, shardsToConsider);
		if (shard == null) {
			return null;
		} else if (shard.getShardIds().size() == 1) {
			return shard.getShardIds().iterator().next();
		} else {
			IdGenerator idGenerator = shardedSqlSessionFactory.getIdGenerator();
			if (idGenerator != null) {
				return idGenerator.extractShardId(this.extractId(parameter));
			} else {
				// TODO(tomislav): also use shard resolution strategy if it returns only 1 shard;
				// throw this error in config instead of here
				throw new RuntimeException(
						"Can not use virtual sharding with non-shard resolving id gen");
			}
		}
	}

	/**
	 * 通过分区选择策略为对象选择分区
	 * 
	 * @param obj
	 *            对象
	 * @return 逻辑分区
	 */
	private ShardId selectShardIdForNewObject(String statement, Object obj) {
		ShardId shardId = shardStrategy.getShardSelectionStrategy().selectShardIdForNewObject(
				statement, obj);
		log.debug(String.format("Selected shard %s for object of type %s", shardId, obj.getClass()
				.getName()));
		return shardId;
	}

	List<ShardId> selectShardIdsFromShardResolutionStrategyData(ShardResolutionStrategyData srsd) {
		IdGenerator idGenerator = shardedSqlSessionFactory.getIdGenerator();
		if ((idGenerator != null) && (srsd.getId() != null)) {
			//
			return Collections.singletonList(idGenerator.extractShardId(srsd.getId()));
		}
		return shardStrategy.getShardResolutionStrategy()
				.selectShardIdsFromShardResolutionStrategyData(srsd);
	}

	private <T> T applyGetOperation(ShardOperation<T> shardOp, ShardResolutionStrategyData srsd) {
		List<ShardId> shardIds = selectShardIdsFromShardResolutionStrategyData(srsd);
		return shardStrategy.getShardAccessStrategy().<T> apply(
				this.shardIdListToShardList(shardIds),
				shardOp,
				new FirstNonNullResultExitStrategy<T>(),
				new ExitOperationsSelectCollector(new AdHocSelectFactoryImpl(
						srsd.getStatement(), srsd.getParameter(), null, RowBounds.DEFAULT), shardStrategy.getShardReduceStrategy()));
	}

	// implements from SqlSession

	
	public <T> T selectOne(String statement) {
		return this.<T> selectOne(statement, null);
	}

	
	public <T> T selectOne(final String statement, final Object parameter) {
		if (statement.endsWith("getById") && parameter != null) {
			ShardOperation<T> shardOp = new ShardOperation<T>() {
				public T execute(SqlSession session, ShardId shardId) {
					return session.<T> selectOne(statement,
							ParameterUtil.resolve(parameter, shardId));
				}

				public String getOperationName() {
					return "selectOne(String statement, Object parameter)";
				}
			};
			Serializable id = this.extractId(parameter);

			Assert.notNull(id, "When get entity by Id, Id can not be null");

			return this.<T> applyGetOperation(shardOp, new ShardResolutionStrategyDataImpl(
					statement, parameter, id));
		}

		// 从Resolution策略获取
		List<Shard> potentialShards = determineShardsViaResolutionStrategyWithReadOperation(
				statement, parameter);

		Assert.notNull(potentialShards, "ShardResolutionStrategy returnd value cann't be null");

		return new ShardSelectImpl(potentialShards, new AdHocSelectFactoryImpl(statement,
				parameter, null, null), shardStrategy.getShardAccessStrategy(),
				shardStrategy.getShardReduceStrategy()).<T> getSingleResult();

	}

	
	public <E> List<E> selectList(String statement) {
		return this.<E> selectList(statement, null);
	}

	
	public <E> List<E> selectList(String statement, Object parameter) {
		return this.<E> selectList(statement, parameter, RowBounds.DEFAULT);
	}

	
	public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
		List<ShardId> shardIds = Lists.newArrayList();

		List<Shard> potentialShards = determineShardsViaResolutionStrategyWithReadOperation(
				statement, parameter);

		if (potentialShards != null) {
			for (Shard shard : potentialShards) {
				shardIds.addAll(shard.getShardIds());
			}
		} else {
			//
			ShardId shardId = this.getShardIdForStatementOrParameter(statement, parameter);
			shardIds = Lists.newArrayList(shardId);
		}

		Assert.isTrue(!shardIds.isEmpty());

		Assert.notNull(potentialShards, "ShardResolutionStrategy returnd value cann't be null");

		return new ShardSelectImpl(potentialShards, new AdHocSelectFactoryImpl(statement,
				parameter, null, rowBounds), shardStrategy.getShardAccessStrategy(),
				shardStrategy.getShardReduceStrategy()).<E> getResultList();
	}

	
	public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
		return this.<K, V> selectMap(statement, null, mapKey);
	}

	
	public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
		return this.<K, V> selectMap(statement, parameter, mapKey, RowBounds.DEFAULT);
	}

	
	public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey,
			RowBounds rowBounds) {
		return new ShardSelectImpl(shards, new AdHocSelectFactoryImpl(statement, parameter, mapKey,
				rowBounds), shardStrategy.getShardAccessStrategy(),
				shardStrategy.getShardReduceStrategy()).getResultMap();
	}

	
	public int insert(String statement) {
		return this.insert(statement, Maps.newHashMap());
	}

	
	public int insert(String statement, Object parameter) {
		ShardId shardId = this.selectShardIdForNewObject(statement, parameter);
		if (shardId == null) {
			shardId = this.getShardIdForStatementOrParameter(statement, parameter);
		}

		Assert.notNull(shardId);

		// 设置当前分区id
		setCurrentSubgraphShardId(shardId);

		log.debug(String.format("Inserting object of type %s to shard %s", parameter.getClass(),
				shardId));

		SqlSession session = shardIdsToShards.get(shardId).establishSqlSession();

		IdGenerator idGenerator = shardedSqlSessionFactory.getIdGenerator();
		if (idGenerator != null) {
			//TODO 生成主键 DB生成主键是用专有session？
			Serializable id = idGenerator.generate(session, parameter);

			log.debug(String
					.format("Generating id for object %s ,the type of IdGenerator is %s and generated Id is %s.",
							parameter.getClass(), idGenerator.getClass(), id));

			ParameterUtil.generatePrimaryKey(parameter, id);
		}

		return session.insert(statement, ParameterUtil.resolve(parameter, shardId));
	}

	
	public int update(String statement) {
		return this.update(statement, Maps.newHashMap());
	}

	
	public int update(String statement, Object parameter) {
		List<ShardId> shardIds = Lists.newArrayList();

		List<Shard> potentialShards = determineShardsViaResolutionStrategyWithWriteOperation(
				statement, parameter);

		if (potentialShards != null) {
			for (Shard shard : potentialShards) {
				shardIds.addAll(shard.getShardIds());
			}
		} else {
			//
			ShardId shardId = this.getShardIdForStatementOrParameter(statement, parameter);
			shardIds = Lists.newArrayList(shardId);
		}

		Assert.isTrue(!shardIds.isEmpty());

		int rows = 0;
		for (ShardId shardId : shardIds) {
			rows += shardIdsToShards.get(shardId).establishSqlSession()
					.update(statement, ParameterUtil.resolve(parameter, shardId));
			log.debug(String.format("Updateing object of type %s to shard %s",
					parameter == null ? parameter : parameter.getClass(), shardId));
		}

		return rows;
	}

	List<Shard> determineShardsViaResolutionStrategyWithWriteOperation(String statement,
			Object parameter) {
		Serializable id = this.extractId(parameter);
		return this.determineShardsObjectsViaResolutionStrategy(statement, parameter, id);
	}

	List<Shard> determineShardsViaResolutionStrategyWithReadOperation(String statement,
			Object parameter) {
		return this.determineShardsObjectsViaResolutionStrategy(statement, parameter, null);
	}

	/**
	 * 通过statement和parameter确定分区 如果parameter中可以提取出主键ID,首先通过ID去确定唯一分区
	 */
	private List<Shard> determineShardsObjectsViaResolutionStrategy(String statement,
			Object parameter, Serializable id) {
		ShardResolutionStrategyData srsd = new ShardResolutionStrategyDataImpl(statement,
				parameter, id);
		List<ShardId> shardIds = this.selectShardIdsFromShardResolutionStrategyData(srsd);
		return shardIdListToShardList(shardIds);
	}

	/**
	 * 获取对象主键值
	 */
	Serializable extractId(Object obj) {
		if (obj != null) {
			if (obj instanceof String || obj instanceof Number) {
				// 当参数为Number/String类型时是否可以认为是主键？
				return (Serializable) obj;
			}

			return ParameterUtil.extractPrimaryKey(obj);
		}
		return null;
	}

	
	public int delete(String statement) {
		return delete(statement, Maps.newHashMap());
	}

	
	public int delete(String statement, Object parameter) {
		List<ShardId> shardIds = Lists.newArrayList();

		List<Shard> potentialShards = determineShardsViaResolutionStrategyWithWriteOperation(
				statement, parameter);
		if (potentialShards != null) {
			for (Shard shard : potentialShards) {
				shardIds.addAll(shard.getShardIds());
			}
		} else {
			// 此种情况下按先从主分区查询statement如果不存在则查询全部分区来定位
			ShardId shardId = this.getShardIdForStatementOrParameter(statement, parameter);
			shardIds = Lists.newArrayList(shardId);
		}

		Assert.isTrue(!shardIds.isEmpty());

		int rows = 0;
		for (ShardId shardId : shardIds) {
			rows += shardIdsToShards.get(shardId).establishSqlSession()
					.delete(statement, ParameterUtil.resolve(parameter, shardId));
			log.debug(String.format("Deleting object of type %s to shard %s", parameter, shardId));
		}
		return rows;
	}

	
	public void commit() {
		commit(false);
	}

	
	public void commit(boolean force) {
//		throw new UnsupportedOperationException(
//				"Manual commit is not allowed over a Spring managed SqlSession");
//		for (Shard shard : this.getShards()) {
//			SqlSession session = shard.getSqlSession();
//			if (session != null) {
//				session.commit(force);
//			}
//		}
	}

	
	public void rollback() {
		rollback(false);
	}

	
	public void rollback(boolean force) {
//		for (Shard shard : this.getShards()) {
//			SqlSession session = shard.getSqlSession();
//			if (session != null) {
//				session.rollback(force);
//			}
//		}
	}

	
	public List<BatchResult> flushStatements() {
		return null;
	}

	
	public void close() {
//		for (Shard shard : this.getShards()) {
//			SqlSession session = shard.getSqlSession();
//			if (session != null) {
//				session.close();
//			}
//		}
	}

	
	public void clearCache() {
		for (Shard shard : this.getShards()) {
			SqlSession session = shard.establishSqlSession();
			if (session != null) {
				session.clearCache();
			}
		}
	}

	
	public <T> T getMapper(Class<T> type) {
		for (Shard shard : this.getShards()) {
			if (shard.hasMapper(type)) {
				return shard.establishSqlSession().getMapper(type);
			}
		}

		throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
	}

	
	public void select(String statement, ResultHandler handler) {
		throw new UnsupportedOperationException(
				"opration select is not allowed over a ShardedSqlSession");
	}

	
	public void select(String statement, Object parameter, ResultHandler handler) {
		throw new UnsupportedOperationException(
				"opration select is not allowed over a ShardedSqlSession");
	}

	
	public void select(String statement, Object parameter, RowBounds rowBounds,
			ResultHandler handler) {
		throw new UnsupportedOperationException(
				"opration select is not allowed over a ShardedSqlSession");
	}

	
	public Configuration getConfiguration() {
		throw new UnsupportedOperationException(
				"Manual get configuration is not allowed over a Spring managed SqlSession");
	}

	
	public Connection getConnection() {
		throw new UnsupportedOperationException(
				"Manual get connection is not allowed over a Spring managed SqlSession");
	}

	// ~~~~~~~~~~~~~~~
	public static ShardId getCurrentSubgraphShardId() {
		return currentSubgraphShardId.get();
	}

	public static void setCurrentSubgraphShardId(ShardId shardId) {
		currentSubgraphShardId.set(shardId);
	}

}
