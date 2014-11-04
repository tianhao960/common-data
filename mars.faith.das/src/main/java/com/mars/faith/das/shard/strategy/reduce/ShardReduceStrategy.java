package com.mars.faith.das.shard.strategy.reduce;

import java.util.List;

import org.apache.ibatis.session.RowBounds;

/**
 * 分区结果集合并策略
 * @author kriswang
 *
 */
public interface ShardReduceStrategy {
	/**
	 * 计算结果集
	 * @param statement	
	 * @param parameter
	 * @param rowBounds
	 * @param values noNullList 
	 * @return
	 */
	List<Object> reduce(String statement, Object parameter, RowBounds rowBounds, List<Object> values);
}
