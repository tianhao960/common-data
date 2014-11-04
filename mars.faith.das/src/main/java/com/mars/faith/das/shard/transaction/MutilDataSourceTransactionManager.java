package com.mars.faith.das.shard.transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import lombok.extern.apachecommons.CommonsLog;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

/**
 * 
 * @author kriswang
 * 
 */
@CommonsLog
public class MutilDataSourceTransactionManager implements PlatformTransactionManager, InitializingBean {

	/**
	 * datasource(s)
	 */
	private List<DataSource> dataSources;

	private Map<DataSource, DataSourceTransactionManager> transactionManagers = new HashMap<DataSource, DataSourceTransactionManager>();

	/**
	 * 提交统计
	 */
	private AtomicInteger commitCount = new AtomicInteger();

	/**
	 * 回滚统计
	 */
	private AtomicInteger rollbackCount = new AtomicInteger();

	public void setDataSources(final List<DataSource> dataSources) {
		this.dataSources = dataSources;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notEmpty(this.dataSources, "data source can't be null");

		for (DataSource dataSource : dataSources) {
			DataSourceTransactionManager txManager = new DataSourceTransactionManager(dataSource);
			transactionManagers.put(dataSource, txManager);
		}

		this.addShutdownHook();
	}

	public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
		MultiDataSourcesTransactionStatus transactionStatus = new MultiDataSourcesTransactionStatus();

		log.debug("Operation '" + definition.getName() + "' starting transaction.");

		for (DataSource dataSource : dataSources) {
			DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition(definition);
			defaultTransactionDefinition.setName(definition.getName());

			PlatformTransactionManager txManager = this.transactionManagers.get(dataSource);
			TransactionStatus status = txManager.getTransaction(defaultTransactionDefinition);

			TransactionSynchronizationManager.setCurrentTransactionName(defaultTransactionDefinition.getName());

			transactionStatus.put(dataSource, status);
		}

		return transactionStatus;

	}

	public void commit(TransactionStatus status) throws TransactionException {
		Throwable ex = null;

		for (int i = dataSources.size() - 1; i >= 0; i--) {
			DataSource dataSource = dataSources.get(i);
			try {
				commitCount.addAndGet(1);

				log.debug("Committing JDBC transaction");

				DataSourceTransactionManager txManager = this.transactionManagers.get(dataSource);

				TransactionStatus transactionStatus = ((MultiDataSourcesTransactionStatus) status).get(dataSource);
				txManager.commit(transactionStatus);

				log.debug("Commit JDBC transaction success");
			} catch (Throwable e) {
				log.debug("Could not commit JDBC transaction", e);
				ex = e;
			} finally {
				commitCount.addAndGet(-1);
			}
		}

		if (ex != null) {
			throw new RuntimeException(ex);
		}
	}

	public void rollback(TransactionStatus status) throws TransactionException {
		Throwable ex = null;

		// Cannot deactivate transaction synchronization - not active
		// Collections.reverse(dataSources);
		for (int i = dataSources.size() - 1; i >= 0; i--) {
			DataSource dataSource = dataSources.get(i);
			try {
				log.debug("Rolling back JDBC transaction");

				rollbackCount.addAndGet(1);

				DataSourceTransactionManager txManager = this.transactionManagers.get(dataSource);
				TransactionStatus currentStatus = ((MultiDataSourcesTransactionStatus) status).get(dataSource);

				txManager.rollback(currentStatus);

				log.info("Roll back JDBC transaction success");
			} catch (Throwable e) {
				log.info("Could not roll back JDBC transaction", e);
				ex = e;
			} finally {
				rollbackCount.addAndGet(-1);
			}
		}

		if (ex != null) {
			throw new RuntimeException(ex);
		}

	}

	private void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@SuppressWarnings("static-access")
			public void run() {
				// rollback 和 commit如果不为0
				while (commitCount.get() != 0) {
					log.info("Waiting for commit transaction.");
					try {
						Thread.currentThread().sleep(1);
					} catch (InterruptedException e) {
						log.warn("interrupted when shuting down the query executor:\n{}", e);
					}
				}
				while (rollbackCount.get() != 0) {
					log.info("Waiting for rollback transaction.");
					try {
						Thread.currentThread().sleep(1);
					} catch (InterruptedException e) {
						log.warn("interrupted when shuting down the query executor:\n{}", e);
					}
				}
				log.info("Transaction success.");
			}
		});
	}
}
