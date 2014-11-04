package com.mars.faith.das.shard.strategy.exit;

import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;

/**
 * Classes that implement this interface are designed to manage the results of a
 * incomplete execution of a query/critieria. For example, with averages the
 * result of each query/critieria should be a list objects on which to calculate
 * the average, rather than the avgerages on each shard. Or the the sum of
 * maxResults(200) should be the sum of only 200 results, not the sum of the
 * sums of 200 results per shard.
 * 
 * @author kriswang
 * 
 */
public interface ExitOperationsCollector {

	List<Object> apply(List<Object> result);
	
	void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory);
}
