package com.mars.faith.das.shard.strategy.exit.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import com.mars.faith.das.shard.strategy.exit.ExitOperation;
import com.mars.faith.das.shard.strategy.exit.ExitOperationUtils;

/**
 * 
 * @author kriswang
 *
 */
public class RowCountExitOperation implements ExitOperation {

	public List<Object> apply(List<Object> result) {
		List<Object> nonNullResults = ExitOperationUtils.getNonNullList(result);

		return Collections.<Object>singletonList(this.getCount(nonNullResults));
	}

	private Number getCount(List<Object> results) {
		BigDecimal sum = new BigDecimal(0.0);
		if(results != null){
			for (Object obj : results) {
				sum = sum.add(new BigDecimal(obj.toString()));
			}

			Object obj = results.get(0);
			if(obj instanceof Integer){
				return sum.intValue();
			}else if(obj instanceof Long){
				return sum.longValue();
			}
		}
		return sum;
	}

}
