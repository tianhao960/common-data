package com.mars.faith.das.mybatis.strategy.impl;

import com.mars.faith.das.mybatis.strategy.ShardStrategy;

/**
 * 不进行分表的策略，供测试用
 * @author kriswang
 *
 */
public class NoShardStrategy implements ShardStrategy {

	public String getTargetTableName(String baseTableName, Object param, String mapperId) {
		return baseTableName;
	}

}
