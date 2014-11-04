package com.mars.faith.das.shard.select.impl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.mars.faith.das.shard.Shard;
import com.mars.faith.das.shard.ShardId;
import com.mars.faith.das.shard.ShardOperation;
import com.mars.faith.das.shard.select.SelectFactory;
import com.mars.faith.das.shard.select.ShardSelect;
import com.mars.faith.das.shard.strategy.access.ShardAccessStrategy;
import com.mars.faith.das.shard.strategy.exit.impl.ConcatenateListsExitStrategy;
import com.mars.faith.das.shard.strategy.exit.impl.ExitOperationsSelectCollector;
import com.mars.faith.das.shard.strategy.exit.impl.SelectOneExitStrategy;
import com.mars.faith.das.shard.strategy.reduce.ShardReduceStrategy;
import com.mars.faith.das.shard.util.ParameterUtil;

/**
 * 
 * @author kriswang
 *
 */
public class ShardSelectImpl implements ShardSelect {
	
	private final List<Shard> shards;
	private final SelectFactory selectFactory;
	private final ShardAccessStrategy shardAccessStrategy;
	private final ExitOperationsSelectCollector selectCollector;
	
	public ShardSelectImpl(List<Shard> shards, SelectFactory selectFactory,
			ShardAccessStrategy shardAccessStrategy, ShardReduceStrategy shardReduceStrategy) {
		this.shards = shards;
		this.selectFactory = selectFactory;
		this.shardAccessStrategy = shardAccessStrategy;
		this.selectCollector = new ExitOperationsSelectCollector(selectFactory, shardReduceStrategy);
	}

	@SuppressWarnings("unchecked")
	public <E> List<E> getResultList() {
		ShardOperation<List<Object>> shardOp = new ShardOperation<List<Object>>() {
			public List<Object> execute(SqlSession session, ShardId shardId) {

				return session.selectList(selectFactory.getStatement(),
						ParameterUtil.resolve(selectFactory.getParameter(), shardId), 
						selectFactory.getRowBounds());
			}

			public String getOperationName() {
				return "getResultList()";
			}
		};

		return (List<E>) shardAccessStrategy.apply(shards, shardOp,
				new ConcatenateListsExitStrategy(), selectCollector);
	}

	public <K, V> Map<K, V> getResultMap() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	public <T> T getSingleResult() {

		ShardOperation<Object> shardOp = new ShardOperation<Object>() {
			public Object execute(SqlSession session, ShardId shardId) {

				return session.selectOne(selectFactory.getStatement(),
						ParameterUtil.resolve(selectFactory.getParameter(), shardId));
			}

			public String getOperationName() {
				return "getSingleResult()";
			}
		};

		return (T) shardAccessStrategy.apply(
				shards,
				shardOp,
				new SelectOneExitStrategy(), selectCollector);
	}

}
