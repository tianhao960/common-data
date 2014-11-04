/*********************************************************************************
*
* <p>
* Perforce File Stats:
* <pre>
* $Id: //brazil/src/appgroup/appgroup/libraries/FBAMICDataAccessLayer/mainline/src/com/amazon/fba/mic/dao/finder/FinderExecutor.java#2 $
* $DateTime: 2013/01/30 06:17:51 $
* $Change: 6865744 $
* </pre>
* </p>
*
* @author $Author: wdong $
* @version $Revision: #2 $
*
* Copyright Notice
*
* This file contains proprietary information of Amazon.com.
* Copying or reproduction without prior written approval is prohibited.
*
* Copyright (c) 2012 Amazon.com.  All rights reserved.
*
*********************************************************************************/
package com.mars.faith.das.dao.finder;

import java.lang.reflect.Method;

import com.mars.faith.das.dao.exception.DataAccessDaoException;

/**
 * @author wdong
 * @param <T>
 */
public interface FinderExecutor<T> {

    Object executeFinder(Method method, Object[] queryArgs) throws DataAccessDaoException;

}