/*********************************************************************************
*
* <p>
* Perforce File Stats:
* <pre>
* $Id: //brazil/src/appgroup/appgroup/libraries/FBAMICDataAccessLayer/mainline/src/com/amazon/fba/mic/dao/utils/MyBatisSQL.java#1 $
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author wdong
 *
 */
public class MyBatisSQL {

    private String sql;

    private Object[] parameters;

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        if (parameters == null || sql == null) {
            return "";
        }
        List<Object> parametersArray = Arrays.asList(parameters);
        List<Object> list = new ArrayList<Object>(parametersArray);
        while (sql.indexOf("?") != -1 && list.size() > 0 && parameters.length > 0) {
            if(list.get(0) == null) {
                list.remove(0);
                continue;
            }
            sql = sql.replaceFirst("\\?", list.get(0).toString());
            list.remove(0);
        }
        return sql.replaceAll("(\r?\n(\\s*\r?\n)+)", "\r\n");
    }

}
