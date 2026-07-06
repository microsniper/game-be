package com.sniper.game.wordgame.config;

import com.sniper.game.wordgame.interceptor.TokenInterceptor;
import com.sniper.game.wordgame.interceptor.SignInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 *
 * @author sniper
 * @since 1.0
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final TokenInterceptor tokenInterceptor;
    private final SignInterceptor signInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册防篡改签名拦截器（针对所有 /api/game/ 下的接口，包括登录接口防止暴力刷量）
        registry.addInterceptor(signInterceptor)
                .addPathPatterns("/api/game/**");

        // 注册 Token 登录拦截器
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/game/login");
    }

}
