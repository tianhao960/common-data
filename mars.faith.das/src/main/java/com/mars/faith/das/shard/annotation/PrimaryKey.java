package com.mars.faith.das.shard.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author kriswang
 *
 */
/**
 * 标注主键（暂时只支持Long,Integer,String 3种类型）
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PrimaryKey {
	String value() default "";
}
