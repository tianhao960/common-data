package com.mars.faith.datasource;

import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.extern.log4j.Log4j;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * Override close method to resolve jdbc driver unregister problem.
 * see: {@linkplain https://issues.apache.org/jira/browse/DBCP-332}
 * @author kriswang
 *
 */
@Log4j
public class FixedBasicDataSource extends BasicDataSource {

	public synchronized void close() throws SQLException {
		log.info("==============> " + url);
		DriverManager.deregisterDriver(DriverManager.getDriver(url));
		super.close();
	}
}
