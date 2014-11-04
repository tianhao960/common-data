/*********************************************************************************
*
* <p>
* Perforce File Stats:
* <pre>
* $Id: //brazil/src/appgroup/appgroup/libraries/FBAMICDataAccessLayer/mainline/src/com/amazon/fba/mic/dao/namingstrategy/impl/Operation.java#2 $
* $DateTime: 2013/01/30 07:57:52 $
* $Change: 6866073 $
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
package com.mars.faith.das.dao.namingstrategy.impl;

public interface Operation {
    public static final String RETRIEVE = "retrieve";
    public static final String CREATE = "create";
    public static final String UPDATE = "update";
    public static final String DELETE = "delete";
    public static final String COUNT = "count";
    public static final String QUERY = "query";
    public static final String BATCH = "batch";
    public static final String DOT = ".";
}