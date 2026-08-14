package com.sniper.game.wordgame.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口名称注解：标注在 Controller 方法上，供日志切面打印中文接口名
 *
 * @author sniper
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiName {

    /** 接口名称，例如：登录接口 */
    String value();
}
