package com.mars.faith.das.dao.utils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * Convert boolean to character 'Y' or 'N'
 * @author wdong
 *
 */
public class BooleanToCharTypeHandler extends BaseTypeHandler<Boolean> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException {
        if (jdbcType == null) {
            ps.setString(i, parameter == true ? "Y" : "N");
        } else {
            ps.setObject(i, parameter == true ? "Y" : "N", jdbcType.TYPE_CODE);
        }
    }

    @Override
    public Boolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
    	String value = rs.getString(columnName);
    	if(null == value) {
    		return false;
    	}
    	return value.equals("Y")? true : false;
    }

    @Override
    public Boolean getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    	String value = rs.getString(columnIndex);
    	if(null == value) {
    		return false;
    	}
    	return value.equals("Y")? true : false;
    }

    @Override
    public Boolean getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    	String value = cs.getString(columnIndex);
    	if(null == value) {
    		return false;
    	}
    	return value.equals("Y")? true : false;
    }

}
