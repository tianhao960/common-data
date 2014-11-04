package com.mars.faith.das.dao.executor;


/**
 * 
 * @author wdong
 *
 * @param <T>
 */
public interface UpdateExecutor<T> {
    
    public int update(T t);
    
}
