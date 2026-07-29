package com.sniper.game.wordgame.interceptor;

import com.alibaba.fastjson.JSON;
import com.sniper.game.wordgame.config.RepeatedlyReadRequestWrapper;
import com.sniper.game.wordgame.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * 签名防篡改与防重放拦截器
 */
@Slf4j
@Component
public class SignInterceptor implements HandlerInterceptor {

    // 必须和前端保持完全一致
    private static final String SECRET_KEY = "X9vP2xL5mN8qR1sT4wY7zB0cJ3fH6gD9";
    // 请求时间戳的有效范围，这里设为 10 分钟 (600000 毫秒)，兼容设备时钟不准的用户
    private static final long MAX_EXPIRE_TIME = 10 * 60 * 1000;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是 POST 请求或者没有被包装过，直接放行（比如 GET 请求）
        if (!(request instanceof RepeatedlyReadRequestWrapper)) {
            return true;
        }

        String timestampStr = request.getHeader("X-Timestamp");
        String sign = request.getHeader("X-Sign");

        if (StringUtils.isBlank(timestampStr) || StringUtils.isBlank(sign)) {
            log.warn("请求缺少签名参数: uri={}", request.getRequestURI());
            writeError(response, "非法请求，缺少签名");
            return false;
        }

        long timestamp;
        try {
            timestamp = Long.parseLong(timestampStr);
        } catch (NumberFormatException e) {
            writeError(response, "非法的时间戳");
            return false;
        }

        // 1. 防重放校验：判断时间戳是否在有效范围内
        long currentTime = System.currentTimeMillis();
        if (Math.abs(currentTime - timestamp) > MAX_EXPIRE_TIME) {
            log.warn("请求已过期: uri={}, clientTime={}, serverTime={}", request.getRequestURI(), timestamp, currentTime);
            writeError(response, "请求已过期，请同步设备时间");
            return false;
        }

        // 2. 防篡改校验：计算并比对 MD5 签名
        RepeatedlyReadRequestWrapper wrapper = (RepeatedlyReadRequestWrapper) request;
        String body = wrapper.getBody();
        if (body == null) {
            body = "";
        }

        // 签名拼接规则：[Body JSON] + [Timestamp] + [Secret Key]
        String strToSign = body + timestampStr + SECRET_KEY;
        String serverSign = DigestUtils.md5DigestAsHex(strToSign.getBytes(StandardCharsets.UTF_8));

        if (!serverSign.equalsIgnoreCase(sign)) {
            log.warn("签名校验失败. uri={}, clientSign={}, serverSign={}, body={}", request.getRequestURI(), sign, serverSign, body);
            writeError(response, "签名校验失败，数据可能被篡改");
            return false;
        }

        return true;
    }

    private void writeError(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(Result.error(400, message)));
    }
}