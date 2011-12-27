package org.complitex.flexbuh.common.entity;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.10.11 18:12
 */
public class PersonTypeHandler extends BaseTypeHandler implements TypeHandler {
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, ((PersonType)parameter).getCode());
    }

    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return PersonType.get(rs.getInt(columnName));
    }

    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return PersonType.get(cs.getInt(columnIndex));
    }
}