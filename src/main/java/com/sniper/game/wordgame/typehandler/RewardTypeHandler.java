package com.sniper.game.wordgame.typehandler;

import com.sniper.game.wordgame.constant.enums.RewardTypeEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * RewardTypeEnum 与库中小写 code（sun/smash/...）互转：
 * MyBatis 默认按枚举名映射，库里是小写 code，故自定义按 code 匹配。
 */
public class RewardTypeHandler extends BaseTypeHandler<RewardTypeEnum> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, RewardTypeEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getCode());
    }

    @Override
    public RewardTypeEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toEnum(rs.getString(columnName));
    }

    @Override
    public RewardTypeEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toEnum(rs.getString(columnIndex));
    }

    @Override
    public RewardTypeEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toEnum(cs.getString(columnIndex));
    }

    private RewardTypeEnum toEnum(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        return RewardTypeEnum.fromCode(code);
    }
}
