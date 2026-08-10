package com.sniper.game.wordgame.typehandler;

import com.sniper.game.wordgame.constant.enums.ResourceCodeTypeEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ResourceCodeTypeEnum 与库中数字 code（1=金币 2=加果盘 ...）互转：
 * MyBatis 默认按枚举名映射，库里是数字 code，故自定义按 code 匹配。
 */
public class ResourceCodeHandler extends BaseTypeHandler<ResourceCodeTypeEnum> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ResourceCodeTypeEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public ResourceCodeTypeEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        return toEnum(rs.wasNull() ? null : code);
    }

    @Override
    public ResourceCodeTypeEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        return toEnum(rs.wasNull() ? null : code);
    }

    @Override
    public ResourceCodeTypeEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        return toEnum(cs.wasNull() ? null : code);
    }

    private ResourceCodeTypeEnum toEnum(Integer code) {
        if (code == null) {
            return null;
        }
        return ResourceCodeTypeEnum.fromCode(code);
    }
}
