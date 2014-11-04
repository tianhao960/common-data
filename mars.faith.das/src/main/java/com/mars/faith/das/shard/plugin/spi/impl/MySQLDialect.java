package com.mars.faith.das.shard.plugin.spi.impl;

import com.mars.faith.das.shard.plugin.spi.Dialect;

/**
 * MySQL数据库分页方言.
 * 
 * @author kriswang
 * 
 */
public class MySQLDialect implements Dialect {

	public boolean supportLimit() {
		return true;
	}

	public boolean supportOffsetLimit() {
		return true;
	}

	public String getLimitString(String sql, int offset, int limit) {
		StringBuffer pagingSelect = new StringBuffer(sql.length() + 40).append(sql);
		if (offset > 0) {
			return pagingSelect.append(" limit ").append(offset).append(", ").append(limit).toString();
		} else {
			return pagingSelect.append(" limit ").append(limit).toString();
		}
	}

}
