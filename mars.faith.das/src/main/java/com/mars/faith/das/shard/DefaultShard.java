package com.mars.faith.das.shard;

import static org.apache.ibatis.reflection.ExceptionUtil.unwrapThrowable;
import static org.mybatis.spring.SqlSessionUtils.closeSqlSession;
import static org.mybatis.spring.SqlSessionUtils.isSqlSessionTransactional;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Set;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.MyBatisExceptionTranslator;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.dao.support.PersistenceExceptionTranslator;

public class DefaultShard implements Shard {

	private final SqlSessionFactory sqlSessionFactory;

	private final PersistenceExceptionTranslator exceptionTranslator;

	private final SqlSession sqlSessionProxy;

	private final Set<ShardId> shardIds;

	public final ExecutorType executorType = ExecutorType.SIMPLE;

	public DefaultShard(Set<ShardId> shardIds, SqlSessionFactory sqlSessionFactory) {
		this.shardIds = shardIds;
		this.sqlSessionFactory = sqlSessionFactory;
		this.exceptionTranslator = new MyBatisExceptionTranslator(sqlSessionFactory.getConfiguration().getEnvironment()
				.getDataSource(), true);
		this.sqlSessionProxy = (SqlSession) Proxy.newProxyInstance(SqlSessionFactory.class.getClassLoader(),
				new Class[] { SqlSession.class }, new SqlSessionInterceptor());
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return this.sqlSessionFactory;
	}
	
	public SqlSession getSqlSession() {
		return sqlSessionProxy;
	}

	public SqlSession establishSqlSession() {
		return this.sqlSessionProxy;
	}

	public Set<ShardId> getShardIds() {
		return this.shardIds;
	}

	public Collection<String> getMappedStatementNames() {
		return getConfiguration().getMappedStatementNames();
	}

	public boolean hasMapper(Class<?> type) {
		return getConfiguration().hasMapper(type);
	}
	
	private Configuration getConfiguration(){
		return sqlSessionFactory.getConfiguration();
	}

	private class SqlSessionInterceptor implements InvocationHandler {

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			final SqlSession sqlSession = SqlSessionUtils.getSqlSession(DefaultShard.this.sqlSessionFactory,
					DefaultShard.this.executorType, DefaultShard.this.exceptionTranslator);

			try {
				Object result = method.invoke(sqlSession, args);
				if (!isSqlSessionTransactional(sqlSession, DefaultShard.this.sqlSessionFactory)) {
					// force commit even on non-dirty sessions because some
					// databases require
					// a commit/rollback before calling close()
					sqlSession.commit(true);
				}
				return result;
			} catch (Throwable t) {
				Throwable unwrapped = unwrapThrowable(t);
		        if (DefaultShard.this.exceptionTranslator != null && unwrapped instanceof PersistenceException) {
		          Throwable translated = DefaultShard.this.exceptionTranslator.translateExceptionIfPossible((PersistenceException) unwrapped);
		          if (translated != null) {
		            unwrapped = translated;
		          }
		        }
		        throw unwrapped;
			} finally {
				closeSqlSession(sqlSession, DefaultShard.this.sqlSessionFactory);
			}
		}
	}

}
