package com.mars.faith.das.shard.strategy.exit;

import java.util.List;

/**
 * 
 * @author kriswang
 *
 */
public interface ExitOperation {

	List<Object> apply(List<Object> result);
}
