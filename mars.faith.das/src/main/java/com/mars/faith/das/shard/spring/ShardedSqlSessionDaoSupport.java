package com.mars.faith.das.shard.spring;

import java.io.Serializable;

import org.mybatis.spring.support.SqlSessionDaoSupport;

import com.mars.faith.das.shard.util.ReflectionUtils;

/**
 * 
 * @author kriswang
 * 
 */
public abstract class ShardedSqlSessionDaoSupport<T, PK extends Serializable> extends SqlSessionDaoSupport {

	protected final Class<T> entityClass;

	protected final String selectOneStatement;

	protected final String insertStatement;
	protected final String deleteStatement;

	public ShardedSqlSessionDaoSupport() {
		this.entityClass = ReflectionUtils.getSuperClassGenricType(getClass());
		this.selectOneStatement = "select" + entityClass.getName();
		this.insertStatement = "insert" + entityClass.getName();
		this.deleteStatement = "delete" + entityClass.getName();
	}

	protected abstract String getNamespace();

	public T find(Class<T> entityClass, PK primaryKey) {
		return super.getSqlSession().<T> selectOne(selectOneStatement, primaryKey);
	}

	public T merge(T entity) {
		return null;
	}

	public void persist(T entity) {
		super.getSqlSession().insert(insertStatement, entity);
	}

	public void remove(Object entity) {
		super.getSqlSession().delete(deleteStatement, entity);
	}

}
