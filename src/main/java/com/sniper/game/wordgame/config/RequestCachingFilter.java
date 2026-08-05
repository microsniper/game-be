package com.sniper.game.wordgame.config;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 将 POST 请求的 HttpServletRequest 包装为 RepeatedlyReadRequestWrapper（供签名拦截器重复读 body）。
 * multipart 上传请求不包装：缓存流会导致容器解析不出 file 分片（MissingServletRequestPartException）。
 */
@Component
public class RequestCachingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String contentType = request.getContentType();
        boolean multipart = contentType != null && contentType.toLowerCase().startsWith("multipart/");
        // 只对 /api/game/ 下的非 multipart POST 请求进行包装缓存
        if (!multipart && request.getRequestURI().startsWith("/api/game") && "POST".equalsIgnoreCase(request.getMethod())) {
            RepeatedlyReadRequestWrapper wrappedRequest = new RepeatedlyReadRequestWrapper(request);
            filterChain.doFilter(wrappedRequest, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}