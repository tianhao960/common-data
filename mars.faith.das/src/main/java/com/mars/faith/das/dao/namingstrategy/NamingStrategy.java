/*********************************************************************************
*
* <p>
* Perforce File Stats:
* <pre>
* $Id: //brazil/src/appgroup/appgroup/libraries/FBAMICDataAccessLayer/mainline/src/com/amazon/fba/mic/dao/namingstrategy/NamingStrategy.java#1 $
* $DateTime: 2012/12/19 03:42:21 $
* $Change: 6681389 $
* </pre>
* </p>
*
* @author $Author: wdong $
* @version $Revision: #1 $
*
* Copyright Notice
*
* This file contains proprietary information of Amazon.com.
* Copying or reproduction without prior written approval is prohibited.
*
* Copyright (c) 2012 Amazon.com.  All rights reserved.
*
*********************************************************************************/
package com.mars.faith.das.dao.namingstrategy;

import java.lang.reflect.Method;

/**
 * Name strategy interface, find the prefix of the given DAO method,<br/>
 * then use AOP to invoke different Implementation.
 * 
 * @author wdong
 * @param <T>
 */
public interface NamingStrategy {
    public String queryNameFromMethod(Class<?> targetType, Method method);
}