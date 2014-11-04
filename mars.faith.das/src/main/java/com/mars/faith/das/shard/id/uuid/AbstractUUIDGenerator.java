package com.mars.faith.das.shard.id.uuid;

import java.net.InetAddress;

import com.mars.faith.das.shard.util.BytesHelper;

/**
 * 
 * @author kriswang
 *
 */
public abstract class AbstractUUIDGenerator {

	private static final int IP;
	
	static {
		int ipadd;
		try {
			ipadd = BytesHelper.toInt(InetAddress.getLocalHost().getAddress());
		} catch(Exception e) {
			ipadd = 0;
		}
		IP = ipadd;
	}
	
	private static short counter = (short)0;
	private static final int JVM = (int)(System.currentTimeMillis() >>> 8);
	
	public AbstractUUIDGenerator(){
		
	}
	
	/**
	 * Unique across JVMs on this machine (unless they load this class in the
	 * same quater second - very unlikely)
	 */
	protected int getJVM() {
		return JVM;
	}
	
	protected short getCount() {
		synchronized (AbstractUUIDGenerator.class) {
			if(counter < 0) {
				counter = 0;
			}
			return counter++;
		}
	}
	
	protected int getIP() {
		return IP;
	}
	
	protected short getHiTime() {
		return (short)(System.currentTimeMillis() >>> 32);
	}
	
	protected int getLoTime() {
		return (int)System.currentTimeMillis();
	}
}
