package com.sniper.game.wordgame.interceptor;

import com.alibaba.fastjson.JSON;
import com.sniper.game.wordgame.exception.BusinessException;
import com.sniper.game.wordgame.service.UserService;
import com.sniper.game.wordgame.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Token拦截器
 * 基础版拦截器，后续可以根据业务需要实现具体的 Token 校验逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = UserContext.getToken();
        if (StringUtils.isBlank(token)) {
            log.warn("请求未携带 token: uri={}", request.getRequestURI());
            writeUnauthorized(response, "请先登录");
            return false;
        }

        try {
            Long userId = userService.getUserIdByToken(token);
            if (userId == null) {
                log.warn("token无效或已过期: uri={}", request.getRequestURI());
                writeUnauthorized(response, "登录已过期，请重新登录");
                return false;
            }
            UserContext.setCurrentUserId(userId);
        } catch (BusinessException e) {
            writeUnauthorized(response, e.getMessage());
            return false;
        }
        return true;
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(com.sniper.game.wordgame.vo.Result.error(401, message)));
    }
}
