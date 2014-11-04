package com.mars.faith.das.shard.strategy.access.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import lombok.extern.apachecommons.CommonsLog;

/**
 * Extension of FutureTask that provides slightly different cancel() behavior.
 * We want cancel() to only return true if the task has not yet run.
 * 
 * @author kriswang
 * 
 */
@CommonsLog
public class StartAwareFutureTask extends FutureTask<Void> {
	
	boolean runCalled;
	boolean cancelled;
	
	private final int id;
	
	public StartAwareFutureTask(Callable<Void> callable, int id) {
		super(callable);
		this.id = id;
	}

	public void run() {
		log.debug(String.format("Task %d: Run invoked.", id));
		
		synchronized(this) {
			if(cancelled) {
				log.debug(String.format("Task %d, Task will not run.", id));
				return;
			}
			runCalled = true;
		}
		
		log.debug(String.format("Task %d: Task will run", id));
		super.run();
	}
	
	public synchronized boolean cancel(boolean mayInterruptIfRunning) {
		if(runCalled) {
			return false;
		}
		
		boolean result = superCancel(mayInterruptIfRunning);
		cancelled = true;
		log.debug(String.format("Task %d: Taks cancelled", id));
		
		return result;
	}
	
	public int getId() {
		return id;
	}

	private boolean superCancel(boolean mayInterruptIfRunning) {	
		return super.cancel(mayInterruptIfRunning);
	}

}
