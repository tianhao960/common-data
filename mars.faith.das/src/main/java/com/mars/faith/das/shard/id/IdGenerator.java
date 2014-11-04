package com.mars.faith.das.shard.id;

import java.io.Serializable;

import org.apache.ibatis.session.SqlSession;

import com.mars.faith.das.shard.ShardId;

/**
 * 主键生成器
 * @author kriswang
 *
 */
public interface IdGenerator {
	
	/**
	 * 生成主键
	 * @param sqlSession 当前的{@link SqlSession}
	 * @param object 需要生成主键的对象
	 * @return 生成的主键
	 */
	Serializable generate(SqlSession sqlSession, Object object);
	
	/**
	 * 根据主键提取逻辑分区
	 * @param identifier 主键
	 * @return 逻辑分区 {@link ShardId}
	 */
	ShardId extractShardId(Serializable identifier);
}
