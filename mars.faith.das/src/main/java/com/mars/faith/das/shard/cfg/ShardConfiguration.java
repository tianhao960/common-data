package com.mars.faith.das.shard.cfg;

import java.util.List;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;

import com.mars.faith.das.shard.ShardId;

/**
 * 用于描述分区的配置信息，不同的物理分区可包含不同的{@link SqlSessionFactory}和多个逻辑分区{@link ShardId}
 * 
 * @author kriswang
 * 
 */
public interface ShardConfiguration {
	/**
	 * @return 此物理分区的唯一ID
	 */
	Integer getShardId();

	/**
	 * @return 此物理分区下所属的所有虚拟分区
	 */
	List<ShardId> getShardIds();

	/**
	 * @return 此物理分区所对应的数据源
	 */
	DataSource getShardDataSource();

	/**
	 * @see SqlSessionFactory
	 * @return 此物理分区的{@link SqlSessionFactory}
	 */
	SqlSessionFactory getSqlSessionFactory();
}
