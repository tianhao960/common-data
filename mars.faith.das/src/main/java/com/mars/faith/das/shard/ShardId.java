package com.mars.faith.das.shard;

import java.util.List;

import org.springframework.util.Assert;

import com.mars.faith.das.shard.util.StringUtil;

/**
 * Uniquely identifies a virtual shard
 * 
 * @author kriswang
 * 
 */
public class ShardId {
	private static final String SPLIT = "_";

	private final int shardId;

	private String prefix;

	private String suffix;

	public ShardId(int shardId) {
		this.shardId = shardId;
	}

	public final int getId() {
		return shardId;
	}

	public String getPrefix() {
		if (!StringUtil.isEmpty(prefix)) {
			return prefix + SPLIT;
		}

		return prefix;
	}
	
	public void setPrefix(String prefix) {
		Assert.notNull(prefix, "can not set prefix with value null");
		this.prefix = prefix;
	}
	
	public String getSuffix() {
		if(!StringUtil.isEmpty(suffix)) {
			return SPLIT + suffix;
		}
		
		return suffix;
	}
	
	public void setSuffix(String suffix) {
		Assert.notNull(suffix, "can not set suffix with value null");
		this.suffix = suffix;
	}
	
	public static ShardId findByShardId(List<ShardId> shardIds, int id) {
		for(ShardId shardId : shardIds) {
			if(shardId.getId() == id) {
				return shardId;
			}
		}
		
		throw new MyBatisShardException("Not found shard id{ " + id + "}");
	}
	
	public int hasCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + shardId;
		return shardId;
	}
	
	public boolean equals(Object obj) {
		
		if(null == obj) {
			return false;
		}
		
		if(!(obj instanceof ShardId)) {
			return false;
		}
		
		if(this == obj) {
			return true;
		}
		
		ShardId other = (ShardId)obj;
		if(shardId != other.getId()) {
			return false;
		}
		
		return true;
	}
	
	public String toString() {
		return String.valueOf(shardId);
	}
}
