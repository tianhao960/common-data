package com.mars.faith.das.dao.executor;

import java.lang.reflect.Method;

/**
 * 
 * @author wdong
 *
 */
public interface InternalUpdateExecutor {
    public int internalUpdate(Method method, Object args);
}
