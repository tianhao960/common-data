/*********************************************************************************
*
* <p>
* Perforce File Stats:
* <pre>
* $Id: //brazil/src/appgroup/appgroup/libraries/FBAMICDataAccessLayer/mainline/src/com/amazon/fba/mic/dao/exception/DataAccessDaoException.java#1 $
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
package com.mars.faith.das.dao.exception;

import java.io.PrintWriter;

/**
 * Dao layer exception, we prefer to throw RuntimeException.l
 * 
 * @author wdong
 */
public class DataAccessDaoException extends RuntimeException {

    private static final long serialVersionUID = 4245218165740930684L;

    private Throwable cause;

    public DataAccessDaoException() {
        super();
    }

    public DataAccessDaoException(String message) {
        super(message);
    }

    public DataAccessDaoException(String message, Throwable cause) {
        this(message);
        this.cause = cause;
    }

    public DataAccessDaoException(Throwable cause) {
        this(cause.getClass().getName() + ": " + cause.getMessage());
        this.cause = cause;
    }

    public void printStatkTrace(PrintWriter s) {
        super.printStackTrace();

        if (cause != null) {
            s.println("\n Caused by: ");
            cause.printStackTrace();
        }

        s.flush();
    }

    public static final DataAccessDaoException toFBAMICDataAccessDaoException(Throwable e) {

        if (e instanceof DataAccessDaoException) {
            return (DataAccessDaoException) e;
        } else {
            return new DataAccessDaoException(e);
        }
    }
}