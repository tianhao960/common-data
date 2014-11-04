package com.mars.faith.das.mybatis;

/**
 * 
 * @author kriswang
 * 
 */
public class ShardException extends RuntimeException {

	private static final long serialVersionUID = -1257641598543617320L;

	public ShardException() {
		super();
	}

	public ShardException(String msg) {
		super(msg);
	}

	public ShardException(String msg, Throwable t) {
		super(msg, t);
	}

	public ShardException(Throwable t) {
		super(t);
	}
}
