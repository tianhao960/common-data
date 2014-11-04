package com.mars.faith.das.shard.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定策略
 * @author kriswang
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ShardStrategy {

	public static enum StrategyType {
		None, Selection, Resolution, Reduce
	}
	
	Class<?> mapper();
	
	StrategyType type() default StrategyType.None;
}
