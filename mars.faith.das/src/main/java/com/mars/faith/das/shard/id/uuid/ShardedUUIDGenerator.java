package com.mars.faith.das.shard.id.uuid;

import java.io.Serializable;

import org.apache.ibatis.session.SqlSession;
import org.springframework.util.Assert;

import com.mars.faith.das.shard.ShardId;
import com.mars.faith.das.shard.id.IdGenerator;
import com.mars.faith.das.shard.session.impl.ShardedSqlSessionImpl;

/**
 * 32位UUID
 * @author kriswang
 *
 */
public class ShardedUUIDGenerator extends UUIDHexGenerator implements IdGenerator {

	private int getShardId() {
		ShardId shardId = ShardedSqlSessionImpl.getCurrentSubgraphShardId();
		Assert.notNull(shardId);

		return Integer.valueOf(shardId.getId());
	}

	@Override
	public Serializable generate(SqlSession session, Object object) {
		String id = new StringBuilder(32).append(format((short) getShardId()))
				.append(format(getIP()))
				.append(format((short) (getJVM() >>> 16)))
				.append(format(getHiTime()))
				.append(format(getLoTime()))
				.append(format(getCount())).toString();

		return id;
	}

	
	public ShardId extractShardId(Serializable identifier) {
		String hexId = (String) identifier;
		return new ShardId(Integer.decode("0x" + hexId.substring(0, 4)));
	}

	public static void main(String[] args) throws Exception {
		ShardedSqlSessionImpl.setCurrentSubgraphShardId(new ShardId(0));

		IdGenerator gen = new ShardedUUIDGenerator();
		IdGenerator gen2 = new ShardedUUIDGenerator();

		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			String id = (String) gen.generate(null, null);
			System.out.println(id);
			String id2 = (String) gen2.generate(null, null);
			System.out.println(id2);
		}

	}

}
