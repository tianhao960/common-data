package com.mars.faith.das.dao.utils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * Convert Date to UTC one.
 * 
 * @author wdong
 */
public class UTCDateTypeHandler extends BaseTypeHandler<Date> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType) throws SQLException {
        Calendar utcCalendar = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        ps.setTimestamp(i, new Timestamp(parameter.getTime()), utcCalendar);
    }

    @Override
    public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Calendar utcCalendar = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Timestamp sqlTimestamp = rs.getTimestamp(columnName, utcCalendar);
        return sqlTimestamp;
    }

    @Override
    public Date getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Calendar utcCalendar = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Timestamp sqlTimestamp = rs.getTimestamp(columnIndex, utcCalendar);
        return sqlTimestamp;
    }

    @Override
    public Date getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Calendar utcCalendar = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Timestamp sqlTimestamp = cs.getTimestamp(columnIndex, utcCalendar);
        return sqlTimestamp;
    }
}
