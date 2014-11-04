package com.mars.faith.das.mybatis.converter;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;

/**
 * Delete 语句转换类
 * @author kriswang
 *
 */
public class DeleteSqlConverter extends AbstractSqlConverter {

	@Override
	protected Statement doConvert(Statement statement, Object params, String mapperId) {
		if (!(statement instanceof Delete)) {
			throw new IllegalArgumentException("The argument statement must is instance of Delete.");
		}
		Delete delete = (Delete) statement;

		String name = delete.getTable().getName();
		delete.getTable().setName(this.convertTableName(name, params, mapperId));
		return delete;
	}

}
