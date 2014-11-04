/*********************************************************************************
*
* <p>
* Perforce File Stats:
* <pre>
* $Id: //brazil/src/appgroup/appgroup/libraries/FBAMICDataAccessLayer/mainline/src/com/amazon/fba/mic/dao/GenericDao.java#4 $
* $DateTime: 2013/01/30 07:57:52 $
* $Change: 6866073 $
* </pre>
* </p>
*
* @author $Author: wdong $
* @version $Revision: #4 $
*
* Copyright Notice
*
* This file contains proprietary information of Amazon.com.
* Copying or reproduction without prior written approval is prohibited.
*
* Copyright (c) 2012 Amazon.com.  All rights reserved.
*
*********************************************************************************/
package com.mars.faith.das.dao;

import java.util.List;
import java.util.Map;

import com.mars.faith.das.dao.executor.DeleteExecutor;
import com.mars.faith.das.dao.executor.UpdateExecutor;

/**
 * The common interface for DAO.
 * @author wdong
 * @param <T> Should give a real POJO type
 * @param <PK> Primary Keys
 */
public interface GenericDao<T, PK> extends UpdateExecutor<T>, DeleteExecutor<T> {

    /**
     * Create
     * 
     * @param newInstance
     */
    public int create(T newInstance);
    
    public Long count(T newInstance);
    
    public Long count(Map<?,?> condition);

    public T retrieve(PK pk); 
    
    public List<T> query(T instance);   
    
    /**
     * To print current executing full SQL
     * @param queryId
     * @param args
     * @return
     */
    public String currentSql(String queryId, Object args);

    /**
     * Batch insertion
     * 
     * @param transientObjects
     */
    public void batch(T[] transientObjects);
}