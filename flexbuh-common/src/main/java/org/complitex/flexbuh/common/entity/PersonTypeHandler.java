package org.complitex.flexbuh.common.entity;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.10.11 18:12
 */
public class PersonTypeHandler extends BaseTypeHandler<PersonType> {
    public void setNonNullParameter(PreparedStatement ps, int i, PersonType parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    public PersonType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return PersonType.get(rs.getInt(columnName));
    }

    @Override
    public PersonType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return null;
    }

    public PersonType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return PersonType.get(cs.getInt(columnIndex));
    }
}