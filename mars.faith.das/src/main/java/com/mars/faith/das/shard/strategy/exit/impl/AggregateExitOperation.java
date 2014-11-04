package com.mars.faith.das.shard.strategy.exit.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import lombok.extern.apachecommons.CommonsLog;

import org.springframework.util.Assert;

import com.mars.faith.das.shard.strategy.exit.ExitOperation;
import com.mars.faith.das.shard.strategy.exit.ExitOperationUtils;
import com.mars.faith.das.shard.util.Lists;

/**
 * 
 * @author kriswang
 *
 */
@CommonsLog
public class AggregateExitOperation implements ExitOperation {
	
	private final SupportedAggregations aggregate;
	
	public AggregateExitOperation(String statementSuffix) {
		Assert.notNull(statementSuffix);
		this.aggregate = SupportedAggregations.valueOf(statementSuffix.toUpperCase());
	}

	public List<Object> apply(List<Object> result) {
		if(result.size() == 0) {
			return Lists.newArrayList();
		}
		
		List<Object> nonNullResults = ExitOperationUtils.getNonNullList(result);
		
		switch(aggregate) {
		case MAX:
			return Collections.<Object>singletonList(Collections.max(ExitOperationUtils.getComparableList(nonNullResults)));
		case MIN:
			return Collections.<Object>singletonList(Collections.min(ExitOperationUtils.getComparableList(nonNullResults)));
		case SUM:
			return Collections.<Object>singletonList(getSum(nonNullResults,null).intValue());
		default:
			log.error("Aggregation Projection is unsupported: " + aggregate);
			throw new UnsupportedOperationException(
					"Aggregation Projection is unsupported: " + aggregate);
		}
	}
	
	private BigDecimal getSum(List<Object> results, String fieldName) {
		BigDecimal sum = new BigDecimal(0.0);
		
		for(Object obj : results) {
			Number num = getNumber(obj, fieldName);
			sum.add(new BigDecimal(num.toString()));
		}
		
		return sum;
	}
	
	
	
	private Number getNumber(Object obj, String fieldName) {
		if(fieldName == null || "".equals(fieldName)){
			return (Number)obj;
		}
		return (Number) ExitOperationUtils.getPropertyValue(obj, fieldName);
	}



	private enum SupportedAggregations {
		SUM("sum"), MIN("min"), MAX("max");
		
		private final String aggregate;
		
		private SupportedAggregations(String s) {
			this.aggregate = s;
		}
		
		@SuppressWarnings("unused")
		public String getAggregate() {
			return this.aggregate;
		}
	}

}
