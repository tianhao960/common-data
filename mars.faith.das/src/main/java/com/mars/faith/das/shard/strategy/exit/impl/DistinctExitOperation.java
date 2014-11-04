package com.mars.faith.das.shard.strategy.exit.impl;

import java.util.List;
import java.util.Set;

import com.mars.faith.das.shard.strategy.exit.ExitOperation;
import com.mars.faith.das.shard.strategy.exit.ExitOperationUtils;
import com.mars.faith.das.shard.util.Lists;
import com.mars.faith.das.shard.util.Sets;

/**
 * 
 * @author kriswang
 *
 */
public class DistinctExitOperation implements ExitOperation {

	public List<Object> apply(List<Object> results) {
		Set<Object> uniqueSet = Sets.newHashSet();
	    uniqueSet.addAll(ExitOperationUtils.getNonNullList(results));

	    List<Object> uniqueList = Lists.newArrayList(uniqueSet);

	    return uniqueList;
	}

}
