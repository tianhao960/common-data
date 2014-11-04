package com.mars.faith.das.shard.spring;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.InitializingBean;

/**
 * 
 * @author kriswang
 *
 */
public class ShardedSqlSessionTemplate implements SqlSession, InitializingBean {

	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

	}

	public <T> T selectOne(String statement) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T selectOne(String statement, Object parameter) {
		// TODO Auto-generated method stub
		return null;
	}

	public <E> List<E> selectList(String statement) {
		// TODO Auto-generated method stub
		return null;
	}

	public <E> List<E> selectList(String statement, Object parameter) {
		// TODO Auto-generated method stub
		return null;
	}

	public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
		// TODO Auto-generated method stub
		return null;
	}

	public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
		// TODO Auto-generated method stub
		return null;
	}

	public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
		// TODO Auto-generated method stub
		return null;
	}

	public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
		// TODO Auto-generated method stub
		return null;
	}

	public void select(String statement, Object parameter, ResultHandler handler) {
		// TODO Auto-generated method stub

	}

	public void select(String statement, ResultHandler handler) {
		// TODO Auto-generated method stub

	}

	public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler) {
		// TODO Auto-generated method stub

	}

	public int insert(String statement) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int insert(String statement, Object parameter) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int update(String statement) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int update(String statement, Object parameter) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int delete(String statement) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int delete(String statement, Object parameter) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void commit() {
		// TODO Auto-generated method stub

	}

	public void commit(boolean force) {
		// TODO Auto-generated method stub

	}

	public void rollback() {
		// TODO Auto-generated method stub

	}

	public void rollback(boolean force) {
		// TODO Auto-generated method stub

	}

	public List<BatchResult> flushStatements() {
		// TODO Auto-generated method stub
		return null;
	}

	public void close() {
		// TODO Auto-generated method stub

	}

	public void clearCache() {
		// TODO Auto-generated method stub

	}

	public Configuration getConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T getMapper(Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

	public Connection getConnection() {
		// TODO Auto-generated method stub
		return null;
	}

}
