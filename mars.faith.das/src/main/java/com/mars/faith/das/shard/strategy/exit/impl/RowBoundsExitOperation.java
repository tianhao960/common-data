package com.mars.faith.das.shard.strategy.exit.impl;

import java.util.Collections;
import java.util.List;

import org.apache.ibatis.session.RowBounds;

import com.mars.faith.das.shard.strategy.exit.ExitOperation;

/**
 * 
 * @author kriswang
 *
 */
public class RowBoundsExitOperation implements ExitOperation {

	private final RowBounds rowBounds;

	public RowBoundsExitOperation(RowBounds rowBounds){
		this.rowBounds = rowBounds;
	}

	public List<Object> apply(List<Object> results) {
	    if(results.size() <= rowBounds.getOffset()) {
	      return Collections.emptyList();
	    }

	    results = results.subList(rowBounds.getOffset(), results.size());
	    if(rowBounds.getLimit() < Integer.MAX_VALUE){
	    	results = results.subList(0, Math.min(results.size(), rowBounds.getLimit()));
	    }

		return results;
	}

}
