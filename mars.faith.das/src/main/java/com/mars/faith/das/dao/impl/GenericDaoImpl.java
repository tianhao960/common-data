/*********************************************************************************
 *
 * <p>
 * Perforce File Stats:
 * <pre>
 * $Id: //brazil/src/appgroup/appgroup/libraries/FBAMICDataAccessLayer/mainline/src/com/amazon/fba/mic/dao/impl/GenericDaoImpl.java#5 $
 * $DateTime: 2013/03/04 03:27:29 $
 * $Change: 7030691 $
 * </pre>
 * </p>
 *
 * @author $Author: wdong $
 * @version $Revision: #5 $
 *
 * Copyright Notice
 *
 * This file contains proprietary information of Amazon.com.
 * Copying or reproduction without prior written approval is prohibited.
 *
 * Copyright (c) 2012 Amazon.com.  All rights reserved.
 *
 *********************************************************************************/
package com.mars.faith.das.dao.impl;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import com.mars.faith.das.dao.GenericDao;
import com.mars.faith.das.dao.exception.DataAccessDaoException;
import com.mars.faith.das.dao.executor.InternalCreateExecutor;
import com.mars.faith.das.dao.executor.InternalDeleteExecutor;
import com.mars.faith.das.dao.executor.InternalUpdateExecutor;
import com.mars.faith.das.dao.finder.FinderExecutor;
import com.mars.faith.das.dao.namingstrategy.NamingStrategy;
import com.mars.faith.das.dao.namingstrategy.impl.Operation;
import com.mars.faith.das.dao.utils.MyBatisSQL;
import com.mars.faith.das.dao.utils.ParamMethod;

/**
 * The realization of GenericDao interface
 * 
 * @author wdong
 * @param <T>
 * @param <PK>
 *            Primary Keys.
 */
public class GenericDaoImpl<T, PK> extends
		SqlSessionDaoSupport implements GenericDao<T, PK>, FinderExecutor<T>, InternalUpdateExecutor, InternalDeleteExecutor,InternalCreateExecutor {

	private static final Logger LOGGER = Logger.getLogger(GenericDaoImpl.class);

	/**
	 * As for this property, you can provide the POJO class that has the same
	 * name with XML configuration or Mapper interface that using MyBatis
	 * Annotations.
	 * DomainObject class for XML configuration approach and interface for MyBatis annotation approach.
	 * This should be refactor because Class<?> will cause confuse. I think we can define our own schema for this function and help users to 
	 * validate type at spring loading time instead of runtime.
	 */
	private Class<?> type; 
	private NamingStrategy namingStrategy;

	public GenericDaoImpl() {
		super();
	}

	public GenericDaoImpl(Class<?> type) {
		this.type = type;
	}

	protected void initDao() throws Exception {
		if (type != null && type.isInterface()) {
			this.getSqlSession().getConfiguration().addMapper(type);
		}
	}

	public NamingStrategy getNamingStrategy() {
		return namingStrategy;
	}

	public void setNamingStrategy(NamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
	}

	
	public Object executeFinder(Method method, Object[] queryArgs) throws DataAccessDaoException {
		Object args = new ParamMethod(method).getParam(queryArgs);
		String query = getNamingStrategy().queryNameFromMethod(type, method);

		Object results = null;
		try {

			if (List.class.isAssignableFrom(method.getReturnType())) {
				results = getSqlSession().selectList(query, args);
			} else {
				results = getSqlSession().selectOne(query, args);
			}

		} catch (Exception ex) {
			LOGGER.error("Query execution failure [the DAO is " + type.getSimpleName() + "Dao ]" + ex.getMessage());
			// scope.addFailure();
			throw new DataAccessDaoException(ex);
		}
		return results;
	}

	
	public int create(T newInstance) {
		int result = getSqlSession().insert(getType() + Operation.DOT + Operation.CREATE, newInstance);
		return result;
	}
	
	
	public int internalCreate(Method method, Object args) {
		String query = getNamingStrategy().queryNameFromMethod(type, method);
		return getSqlSession().insert(query, args);
	}

	
	public Long count(T newInstance) {
		return getSqlSession().selectOne(getType() + Operation.DOT + Operation.COUNT, newInstance);
	}

	
	public Long count(Map<?, ?> condition) {
		return getSqlSession().selectOne(getType() + Operation.DOT + Operation.COUNT, condition);
	}

	
	public T retrieve(PK pk) {
		Validate.notNull(pk);
		T result = getSqlSession().selectOne(getType() + Operation.DOT + Operation.RETRIEVE, pk);
		return result;
	}

	
	public List<T> query(T instance) {
		Validate.notNull(instance);
		List<T> results = getSqlSession().selectList(getType() + Operation.DOT + Operation.QUERY, instance);
		return results;
	}

	
	public int update(T transientObject) {
		return getSqlSession().update(getType() + Operation.DOT + Operation.UPDATE, transientObject);
	}

	
	public int internalUpdate(Method method, Object args) {
		String query = getNamingStrategy().queryNameFromMethod(type, method);
		return getSqlSession().update(query, args);
	}

	
	public void delete(T persistentObject) {
		getSqlSession().delete(getType() + Operation.DOT + Operation.DELETE, persistentObject);
	}

	
	public void internalDelete(Method method, Object args) {
		String query = getNamingStrategy().queryNameFromMethod(type, method);
		getSqlSession().delete(query, args);
	}

	
	public String currentSql(String queryId, Object queryArgs) {
		MyBatisSQL myBatisSQL = new MyBatisSQL();
		MappedStatement ms = this.getSqlSession().getConfiguration().getMappedStatement(queryId);
		BoundSql boundSql = ms.getBoundSql(queryArgs);
		myBatisSQL.setSql(boundSql.getSql());
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

		if (parameterMappings != null) {

			Map<?, ?> args = null;
			if (queryArgs != null) {
				if (queryArgs instanceof Map) {
					args = (Map<?, ?>) queryArgs;
				} else {
					return boundSql.getSql();
				}
			}

			Object[] parameterArray = new Object[parameterMappings.size()];
			ParameterMapping parameterMapping = null;
			Object value = null;
			Object parameterObject = null;
			MetaObject metaObject = null;
			PropertyTokenizer prop = null;
			String propertyName = null;
			String[] names = null;
			for (int i = 0; i < parameterMappings.size(); i++) {
				parameterMapping = parameterMappings.get(i);
				if (parameterMapping.getMode() != ParameterMode.OUT) {
					propertyName = parameterMapping.getProperty();
					names = propertyName.split("\\.");
					if (propertyName.indexOf(".") != -1 && names.length == 2) {
						parameterObject = args.get(names[0]);
						propertyName = names[1];
					} else if (propertyName.indexOf(".") != -1 && names.length == 3) {
						parameterObject = args.get(names[0]); // map
						if (parameterObject instanceof Map) {
							parameterObject = ((Map<?, ?>) parameterObject).get(names[1]);
						}
						propertyName = names[2];
					} else {
						parameterObject = args.get(propertyName);
					}
					metaObject = args == null ? null : MetaObject.forObject(parameterObject, getSqlSession().getConfiguration().getObjectFactory(), getSqlSession().getConfiguration().getObjectWrapperFactory());
					prop = new PropertyTokenizer(propertyName);
					if (parameterObject == null) {
						value = null;
					} else if (ms.getConfiguration().getTypeHandlerRegistry()
							.hasTypeHandler(parameterObject.getClass())) {
						value = parameterObject;
					} else if (boundSql.hasAdditionalParameter(propertyName)) {
						value = boundSql.getAdditionalParameter(propertyName);
					} else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX)
							&& boundSql.hasAdditionalParameter(prop.getName())) {
						value = boundSql.getAdditionalParameter(prop.getName());
						if (value != null) {
							value = MetaObject.forObject(value,getSqlSession().getConfiguration().getObjectFactory(), getSqlSession().getConfiguration().getObjectWrapperFactory()).getValue(
									propertyName.substring(prop.getName().length()));
						}
					} else {
						value = metaObject == null ? null : metaObject.getValue(propertyName);
					}
					parameterArray[i] = value;
				}
			}
			myBatisSQL.setParameters(parameterArray);
		}
		return myBatisSQL.toString();
	}

	@Transactional	
	public void batch(T[] transientObjects) {
		for (T t : transientObjects) {
			getSqlSession().insert(getType() + Operation.DOT + Operation.BATCH, t);
		}
	}

	private String getType() {
		return type != null ? type.getName() : "";
	}

}
