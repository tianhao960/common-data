package com.mars.faith.das.shard.id.uuid;

import java.io.Serializable;

import org.apache.ibatis.session.SqlSession;

/**
 * 
 * @author kriswang
 * 
 */
public class UUIDHexGenerator extends AbstractUUIDGenerator {

	private String sep = "/";

	protected String format(int intval) {
		String formatted = Integer.toHexString(intval);
		StringBuffer buf = new StringBuffer("00000000");
		buf.replace(8 - formatted.length(), 8, formatted);
		return buf.toString();
	}
	
	protected String format(short shortval) {
		String formatted = Integer.toHexString(shortval);
		StringBuffer buf = new StringBuffer("0000");
		buf.replace(4 - formatted.length(), 4, formatted);
		return buf.toString();
	}
	
	public Serializable generate(SqlSession session, Object obj) {
		return new StringBuffer(36).append(format(getIP())).append(sep)
				.append(format(getJVM())).append(sep)
				.append(format(getHiTime())).append(sep)
				.append(format(getLoTime())).append(sep)
				.append(format(getCount())).toString();
	}
	
}
