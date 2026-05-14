package com.sniper.game.wordgame.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户上下文工具类
 */
public class UserContext {

    private static final String HEADER_TOKEN = "Authorization";
    private static final String USER_ID_ATTR = "currentUserId";

    private UserContext() {
    }

    /**
     * 获取当前请求
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 获取请求头中的 token
     */
    public static String getToken() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        String token = request.getHeader(HEADER_TOKEN);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return token;
    }

    /**
     * 设置当前用户ID
     */
    public static void setCurrentUserId(Long userId) {
        HttpServletRequest request = getRequest();
        if (request != null) {
            request.setAttribute(USER_ID_ATTR, userId);
        }
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        HttpServletRequest request = getRequest();
        return request != null ? (Long) request.getAttribute(USER_ID_ATTR) : null;
    }

}
