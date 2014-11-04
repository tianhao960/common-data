package com.mars.faith.das.shard.select.impl;

import org.apache.ibatis.session.RowBounds;

import com.mars.faith.das.shard.select.SelectFactory;

/**
 * 
 * @author kriswang
 * 
 */
public class AdHocSelectFactoryImpl implements SelectFactory {

	private final String statement;
	private final Object parameter;
	private final String mapKey;
	private final RowBounds rowBounds;

	public AdHocSelectFactoryImpl(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
		this.statement = statement;
		this.parameter = parameter;
		this.mapKey = mapKey;
		this.rowBounds = rowBounds;

	}

	public String getStatement() {
		return statement;
	}

	public Object getParameter() {
		return parameter;
	}

	public String getMapKey() {
		return mapKey;
	}

	public RowBounds getRowBounds() {
		return rowBounds;
	}

}
