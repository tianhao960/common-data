package com.mars.faith.das.shard.strategy.resolution;

import java.io.Serializable;

/**
 * 
 * @author kriswang
 * 
 */
public interface ShardResolutionStrategyData {
	String getStatement();

	Object getParameter();

	Serializable getId();
}
