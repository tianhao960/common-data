package com.mars.faith.das.shard.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.mars.faith.das.shard.ShardedConfiguration;
import com.mars.faith.das.shard.cfg.ShardConfiguration;
import com.mars.faith.das.shard.cfg.impl.ShardConfigurationImpl;
import com.mars.faith.das.shard.id.IdGenerator;
import com.mars.faith.das.shard.session.ShardedSqlSessionFactory;
import com.mars.faith.das.shard.strategy.ShardStrategyFactory;

/**
 * 
 * @author kriswang
 * 
 */
public class ShardedSqlSessionFactoryBean implements FactoryBean<ShardedSqlSessionFactory>, InitializingBean {

	private Resource configLocation;

	private Resource[] mapperLocations;

	private Map<Integer, DataSource> dataSources;

	private Properties configurationProperties;

	private String environment = ShardedSqlSessionFactory.class.getSimpleName();

	// private boolean failFast;

	private Interceptor[] plugins;

	private TypeHandler<?>[] typeHandlers;

	private String typeHandlersPackage;

	private Class<?>[] typeAliases;

	private String typeAliasesPackage;

	private ShardedSqlSessionFactory shardedSqlSessionFactory;

	private ShardStrategyFactory shardStrategyFactory;

	private IdGenerator idGenerator;

	// 直接配置ShardConfiguration
	private List<ShardConfigurationImpl> shardConfigurations;

	
	public void afterPropertiesSet() throws Exception {
		// Assert.notNull(dataSources, "data sources can not be null.");

		List<ShardConfiguration> shardConfigs = new ArrayList<ShardConfiguration>();

		if (CollectionUtils.isEmpty(shardConfigurations)) {
			for (Map.Entry<Integer, DataSource> entry : dataSources.entrySet()) {
				int shardId = entry.getKey(); // 虚拟分区ID
				DataSource dataSource = entry.getValue(); // 虚拟分区所属数据源

				SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
				factoryBean.setConfigLocation(this.configLocation);
				factoryBean.setMapperLocations(this.mapperLocations);
				factoryBean.setDataSource(dataSource);
				factoryBean.setEnvironment(this.environment);
				factoryBean.setConfigurationProperties(this.configurationProperties);
				factoryBean.setPlugins(this.plugins);
				factoryBean.setTypeHandlers(this.typeHandlers);
				factoryBean.setTypeHandlersPackage(this.typeHandlersPackage);
				factoryBean.setTypeAliases(this.typeAliases);
				factoryBean.setTypeAliasesPackage(this.typeAliasesPackage);

				SqlSessionFactory sessionFacotry = factoryBean.getObject();

				shardConfigs.add(new ShardConfigurationImpl(shardId, dataSource, sessionFacotry));
			}
		} else {
			for (ShardConfigurationImpl shardConfiguration : shardConfigurations) {

				Assert.notNull(shardConfiguration.getShardId(), "shard id can not be null.");
				Assert.notNull(shardConfiguration.getShardDataSource(), "data source can not be null.");

				SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
				factoryBean.setConfigLocation(shardConfiguration.getConfigLocation());
				factoryBean.setMapperLocations(shardConfiguration.getMapperLocations());
				factoryBean.setDataSource(shardConfiguration.getShardDataSource());
				factoryBean.setEnvironment(this.environment);
				factoryBean.setConfigurationProperties(this.configurationProperties);
				factoryBean.setPlugins(this.plugins);
				factoryBean.setTypeHandlers(this.typeHandlers);
				factoryBean.setTypeHandlersPackage(shardConfiguration.getTypeHandlersPackage());
				factoryBean.setTypeAliases(this.typeAliases);
				factoryBean.setTypeAliasesPackage(shardConfiguration.getTypeAliasesPackage());

				SqlSessionFactory sessionFacotry = factoryBean.getObject();
				shardConfiguration.setSqlSessionFactory(sessionFacotry);

				shardConfigs.add(shardConfiguration);
			}

		}

		ShardedConfiguration configuration = new ShardedConfiguration(shardConfigs, this.shardStrategyFactory,
				idGenerator);
		shardedSqlSessionFactory = configuration.buildShardedSessionFactory();
	}

	
	public ShardedSqlSessionFactory getObject() throws Exception {
		if (this.shardedSqlSessionFactory == null) {
			afterPropertiesSet();
		}

		return this.shardedSqlSessionFactory;
	}

	
	public Class<?> getObjectType() {
		return this.shardedSqlSessionFactory == null ? ShardedSqlSessionFactory.class : this.shardedSqlSessionFactory
				.getClass();
	}

	
	public boolean isSingleton() {
		return true;
	}

	// Setter from here
	public void setDataSources(final Map<Integer, DataSource> dataSources) {
		this.dataSources = dataSources;
	}

	public void setConfigLocation(Resource configLocation) {
		this.configLocation = configLocation;
	}

	public void setMapperLocations(Resource[] mapperLocations) {
		this.mapperLocations = mapperLocations;
	}

	public void setConfigurationProperties(Properties configurationProperties) {
		this.configurationProperties = configurationProperties;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public void setPlugins(Interceptor[] plugins) {
		this.plugins = plugins;
	}

	public void setTypeHandlers(TypeHandler<?>[] typeHandlers) {
		this.typeHandlers = typeHandlers;
	}

	public void setTypeHandlersPackage(String typeHandlersPackage) {
		this.typeHandlersPackage = typeHandlersPackage;
	}

	public void setTypeAliases(Class<?>[] typeAliases) {
		this.typeAliases = typeAliases;
	}

	public void setTypeAliasesPackage(String typeAliasesPackage) {
		this.typeAliasesPackage = typeAliasesPackage;
	}

	public void setShardStrategyFactory(ShardStrategyFactory shardStrategyFactory) {
		this.shardStrategyFactory = shardStrategyFactory;
	}

	public void setIdGenerator(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	public void setShardConfigurations(List<ShardConfigurationImpl> shardConfigurations) {
		this.shardConfigurations = shardConfigurations;
	}

}
