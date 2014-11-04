package com.mars.faith.das.shard.select;

import org.apache.ibatis.session.RowBounds;

/**
 * 
 * @author kriswang
 *
 */
public interface SelectFactory {
	
	String getStatement();

	Object getParameter();

	String getMapKey();

	RowBounds getRowBounds();
	
}
