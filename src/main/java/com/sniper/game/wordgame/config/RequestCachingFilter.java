package com.sniper.game.wordgame.config;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 将 POST 请求的 HttpServletRequest 包装为 RepeatedlyReadRequestWrapper
 */
@Component
public class RequestCachingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 只对 /api/game/ 下的 POST 请求进行包装缓存
        if (request.getRequestURI().startsWith("/api/game") && "POST".equalsIgnoreCase(request.getMethod())) {
            RepeatedlyReadRequestWrapper wrappedRequest = new RepeatedlyReadRequestWrapper(request);
            filterChain.doFilter(wrappedRequest, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}