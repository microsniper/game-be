package com.sniper.game.wordgame.exception;

import lombok.Getter;

/**
 * 业务异常
 * 用于业务逻辑中抛出，统一错误码管理
 *
 * @author sniper
 * @since 1.0
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    // ========== 常用业务异常 ==========

    /**
     * 用户未登录
     */
    public static BusinessException unauthorized(String message) {
        return new BusinessException(401, message);
    }

    /**
     * 资源不存在
     */
    public static BusinessException notFound(String message) {
        return new BusinessException(404, message);
    }

    /**
     * 参数错误
     */
    public static BusinessException badRequest(String message) {
        return new BusinessException(400, message);
    }

    /**
     * 业务规则冲突
     */
    public static BusinessException conflict(String message) {
        return new BusinessException(409, message);
    }
}
