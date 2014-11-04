/*********************************************************************************
*
* <p>
* Perforce File Stats:
* <pre>
* $Id: //brazil/src/appgroup/appgroup/libraries/FBAMICDataAccessLayer/mainline/src/com/amazon/fba/mic/dao/namingstrategy/impl/NamingStrategyImpl.java#1 $
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
package com.mars.faith.das.dao.namingstrategy.impl;

import java.lang.reflect.Method;

import com.mars.faith.das.dao.namingstrategy.NamingStrategy;

/**
 * Support methods with the prefix [findBy, listBy, get, scrollBy etc.], these
 * methods will be convert to <br/>
 * findBy... Method invocation.
 * 
 * @author wdong
 * @param <T>
 */
public class NamingStrategyImpl implements NamingStrategy {

    public String queryNameFromMethod(Class<?> targetType, Method method) {

        String methodName = method.getName();
        return targetType.getName() + "." + methodName;
    }
}