/*********************************************************************************
*
* <p>
* Perforce File Stats:
* <pre>
* $Id: //brazil/src/appgroup/appgroup/libraries/FBAMICDataAccessLayer/mainline/src/com/amazon/fba/mic/dao/utils/DASBeanUtils.java#1 $
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
package com.mars.faith.das.dao.utils;

import org.springframework.beans.BeanUtils;

import com.mars.faith.das.dao.exception.DataAccessDaoException;

/**
 * Utilities of the Jakarta Commons BeanUtils component, provides easy-to-use
 * wrappers around reflection capabilities.
 * 
 * @author wdong
 * @param <S> Source Object
 * @param <D> Destination Object
 */
public class DASBeanUtils {

    public static <S extends Object, D extends Object> void copyProperties(S srcObject, D destObject) throws DataAccessDaoException {
        try {
            BeanUtils.copyProperties(srcObject, destObject);
        } catch (Exception ex) {
            throw new DataAccessDaoException("Properties copy exception: " + ex.getMessage(), ex);
        }
    }
}