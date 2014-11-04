package com.mars.faith.das.shard;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.apachecommons.CommonsLog;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.util.Assert;

import com.mars.faith.das.shard.cfg.ShardConfiguration;
import com.mars.faith.das.shard.id.IdGenerator;
import com.mars.faith.das.shard.session.ShardedSqlSessionFactory;
import com.mars.faith.das.shard.session.impl.ShardedSqlSessionFactoryImpl;
import com.mars.faith.das.shard.strategy.ShardStrategyFactory;
import com.mars.faith.das.shard.util.Maps;

/**
 * shard plugin configuration
 * 
 * @author kriswang
 * 
 */
@CommonsLog
public class ShardedConfiguration {
	// 分区配置
	private final List<ShardConfiguration> shardConfigs;

	// 用户自定义分区策略
	private final ShardStrategyFactory shardStrategyFactory;

	private final IdGenerator idGenerator;

	// 虚拟分区ids --> 物理分区 ids 映射
	private final Map<Integer, Integer> virtualShardToShardMap;

	// 物理分区ids --> 虚拟分区ids集合 映射
	private final Map<Integer, Set<ShardId>> shardToVirtualShardIdMap;

	public ShardedConfiguration(List<ShardConfiguration> shardConfigs, ShardStrategyFactory shardStrategyFactory,
			IdGenerator idGenerator) {

		this(shardConfigs, shardStrategyFactory, new HashMap<Integer, Integer>(), idGenerator);
	}

	public ShardedConfiguration(List<ShardConfiguration> shardConfigs, ShardStrategyFactory shardStrategyFactory,
			Map<Integer, Integer> virtualShardToShardMap, IdGenerator idGenerator) {

		Assert.notNull(shardConfigs);
		Assert.notNull(shardStrategyFactory);
		Assert.notNull(virtualShardToShardMap);

		this.shardConfigs = shardConfigs;
		this.shardStrategyFactory = shardStrategyFactory;
		this.virtualShardToShardMap = virtualShardToShardMap;
		this.idGenerator = idGenerator;

		if (!virtualShardToShardMap.isEmpty()) {
			// build the map from shard to set of virtual shards
			shardToVirtualShardIdMap = Maps.newHashMap();
			for (Map.Entry<Integer, Integer> entry : virtualShardToShardMap.entrySet()) {
				Set<ShardId> set = shardToVirtualShardIdMap.get(entry.getValue());
				// see if we already have a set of virtual shards
				if (set == null) {
					// we don't, so create it and add it to the map
					set = new HashSet<ShardId>();
					shardToVirtualShardIdMap.put(entry.getValue(), set);
				}
				set.add(new ShardId(entry.getKey()));
			}
		} else {
			shardToVirtualShardIdMap = new HashMap<Integer, Set<ShardId>>();
		}
	}

	public ShardedSqlSessionFactory buildShardedSessionFactory() {
		Map<SqlSessionFactory, Set<ShardId>> sqlSessionFactories = new HashMap<SqlSessionFactory, Set<ShardId>>();

		for (ShardConfiguration config : shardConfigs) {
			// populatePrototypeWithVariableProperties(config);
			// get the shardId from the shard-specific config
			Integer shardId = config.getShardId();
			if (shardId == null) {
				final String msg = "Attempt to build a ShardedSessionFactory using a "
						+ "ShardConfiguration that has a null shard id.";
				log.fatal(msg);
				throw new NullPointerException(msg);
			}
			Set<ShardId> virtualShardIds;
			if (virtualShardToShardMap.isEmpty()) {
				// simple case, virtual and physical are the same
				virtualShardIds = Collections.singleton(new ShardId(shardId));
			} else {
				// get the set of shard ids that are mapped to the physical
				// shard
				// described by this config
				virtualShardIds = shardToVirtualShardIdMap.get(shardId);
			}

			sqlSessionFactories.put(config.getSqlSessionFactory(), virtualShardIds);
		}

		return new ShardedSqlSessionFactoryImpl(sqlSessionFactories, shardStrategyFactory, idGenerator);
	}

}
