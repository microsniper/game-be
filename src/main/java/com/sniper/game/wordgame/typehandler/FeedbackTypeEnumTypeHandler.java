package com.sniper.game.wordgame.typehandler;

import com.sniper.game.wordgame.constant.enums.FeedbackTypeEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FeedbackTypeEnumTypeHandler extends BaseTypeHandler<FeedbackTypeEnum> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, FeedbackTypeEnum parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public FeedbackTypeEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        return rs.wasNull() ? null : FeedbackTypeEnum.fromCode(code);
    }

    @Override
    public FeedbackTypeEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        return rs.wasNull() ? null : FeedbackTypeEnum.fromCode(code);
    }

    @Override
    public FeedbackTypeEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        return cs.wasNull() ? null : FeedbackTypeEnum.fromCode(code);
    }
}
