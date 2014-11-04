package com.mars.faith.das.mybatis.readwrite;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Dynamic data source using round robin algorithm, will be used for key generation service
 * @author kriswang
 *
 */
public class RoundRobinDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		//TODO:(kris)
		return null;
	}

}
