package com.mars.faith.das.shard.select;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author kriswang
 *
 */
public interface ShardSelect {

	<E> List<E> getResultList();

	<K, V> Map<K, V> getResultMap();

	<T> T getSingleResult(); 
	
}
