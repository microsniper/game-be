package com.sniper.game.wordgame.config;

import com.alibaba.fastjson.JSON;
import com.sniper.game.wordgame.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class WebLogAspect {


    @Pointcut("execution(public * com.sniper.game.wordgame.controller.*.*(..))")
    public void webLog() {
    }

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        String url = request != null ? request.getRequestURL().toString() : "";
        String method = request != null ? request.getMethod() : "";
        String ip = getClientIp(request);
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        
        Object[] args = joinPoint.getArgs();
        String requestParams = "";
        try {
            requestParams = JSON.toJSONString(args);
        } catch (Exception e) {
            requestParams = "无法序列化的参数";
        }

        log.info("================ Request Start ================");
        log.info("URL            : \u001B[96m{}\u001B[0m", url);
        log.info("HTTP Method    : {}", method);
        log.info("Class Method   : {}", classMethod);
        log.info("IP             : {}", ip);
        
        Long userId = UserContext.getCurrentUserId();
        if (userId != null) {
            log.info("UserID         : \u001B[93m{}\u001B[0m", userId);
            String openid = UserContext.getCurrentOpenid();
            if (openid != null) {
                log.info("Openid         : {}", openid);
            }
        }
        
        log.info("Request Args   : {}", requestParams);

        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            log.error("Exception    : {}", e.getMessage(), e);
            throw e;
        } finally {
            String responseResult = "";
            try {
                if (result != null) {
                    responseResult = JSON.toJSONString(result);
                }
            } catch (Exception e) {
                responseResult = "无法序列化的响应结果";
            }
            log.info("Response Result: {}", responseResult);
            log.info("Time Cost      : {} ms", System.currentTimeMillis() - startTime);
            log.info("================ Request End ==================");
        }
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，取第一个IP为客户端真实IP
        if (ip != null && ip.indexOf(",") > 0) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }
        return ip;
    }
}
