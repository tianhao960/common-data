package com.mars.faith.das.shard.strategy.exit.impl;

import java.util.Collections;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mars.faith.das.shard.select.SelectFactory;
import com.mars.faith.das.shard.strategy.exit.ExitOperationsCollector;
import com.mars.faith.das.shard.strategy.reduce.ShardReduceStrategy;

/**
 * 
 * @author kriswang
 *
 */
public class ExitOperationsSelectCollector implements ExitOperationsCollector {
	
	private final String statement;
	private final Object parameter;
	private final RowBounds rowBounds;

	private final ShardReduceStrategy shardReduceStrategy;
	
	public ExitOperationsSelectCollector(SelectFactory selectFactory, ShardReduceStrategy shardReduceStrategy){
		this.statement = selectFactory.getStatement();
		this.parameter = selectFactory.getParameter();
		this.rowBounds = selectFactory.getRowBounds();

		this.shardReduceStrategy = shardReduceStrategy;

	}

	public List<Object> apply(List<Object> values) {

		if(!values.isEmpty()){
			//调用reduce策略
			List<Object> results = shardReduceStrategy.reduce(statement, parameter, rowBounds, values);

			values = (results != null) ? results : Collections.emptyList();	//去除结果为null的情况

			if (rowBounds != null && rowBounds != RowBounds.DEFAULT) {
				values = new RowBoundsExitOperation(rowBounds).apply(values);
			}

		}

		return values;
	}

	public void setSqlSessionFactory(SqlSessionFactory sessionFactory) {
		throw new UnsupportedOperationException();
	}


}
