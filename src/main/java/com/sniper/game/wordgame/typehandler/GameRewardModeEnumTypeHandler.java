package com.sniper.game.wordgame.typehandler;

import com.sniper.game.wordgame.constant.enums.GameRewardModeEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GameRewardModeEnumTypeHandler extends BaseTypeHandler<GameRewardModeEnum> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, GameRewardModeEnum parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public GameRewardModeEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        return rs.wasNull() ? null : GameRewardModeEnum.fromCode(code);
    }

    @Override
    public GameRewardModeEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        return rs.wasNull() ? null : GameRewardModeEnum.fromCode(code);
    }

    @Override
    public GameRewardModeEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        return cs.wasNull() ? null : GameRewardModeEnum.fromCode(code);
    }
}
