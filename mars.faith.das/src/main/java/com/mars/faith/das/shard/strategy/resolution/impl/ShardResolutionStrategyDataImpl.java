package com.mars.faith.das.shard.strategy.resolution.impl;

import java.io.Serializable;

import com.mars.faith.das.shard.strategy.resolution.ShardResolutionStrategyData;

/**
 * 
 * @author kriswang
 *
 */
public class ShardResolutionStrategyDataImpl implements ShardResolutionStrategyData {
	private final String statement;

	private final Object parameter;

	private final Serializable id;

	public ShardResolutionStrategyDataImpl(String statement, Object parameter, Serializable id){
		this.statement = statement;
		this.parameter = parameter;
		this.id = id;
	}

	
	public String getStatement() {
		return statement;
	}

	
	public Object getParameter() {
		return parameter;
	}

	
	public Serializable getId() {
		return id;
	}

}
