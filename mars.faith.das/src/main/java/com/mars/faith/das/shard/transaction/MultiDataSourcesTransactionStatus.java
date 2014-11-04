package com.mars.faith.das.shard.transaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.AbstractTransactionStatus;

/**
 * 
 * @author kriswang
 * 
 */
public class MultiDataSourcesTransactionStatus extends AbstractTransactionStatus {

	private Map<DataSource, TransactionStatus> dataSourceTransactionStatusMap = new ConcurrentHashMap<DataSource, TransactionStatus>();

	public boolean isNewTransaction() {
		return true;
	}

	/**
	 * 设置
	 * 
	 * @param dataSource
	 * @param transactionStatus
	 * @return
	 */
	public Map<DataSource, TransactionStatus> put(DataSource dataSource, TransactionStatus transactionStatus) {
		dataSourceTransactionStatusMap.put(dataSource, transactionStatus);
		return dataSourceTransactionStatusMap;
	}

	public TransactionStatus get(DataSource dataSource) {
		return dataSourceTransactionStatusMap.get(dataSource);
	}

}
