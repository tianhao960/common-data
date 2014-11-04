package com.mars.faith.das.shard;

/**
 * 
 * @author kriswang
 *
 */
public class MyBatisShardException extends RuntimeException {

	private static final long serialVersionUID = 3112594785104734126L;

	public MyBatisShardException() {
		super();
	}

	public MyBatisShardException(String msg) {
		super(msg);
	}

	public MyBatisShardException(String msg, Throwable t) {
		super(msg, t);
	}

	public MyBatisShardException(Throwable t) {
		super(t);
	}
}
