package com.sniper.game.wordgame.exception;

import com.sniper.game.wordgame.vo.Result;
import com.sniper.game.wordgame.annotation.ApiName;
import com.sniper.game.wordgame.service.FeishuNotifyService;
import com.sniper.game.wordgame.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author sniper
 * @since 1.0
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final FeishuNotifyService feishuNotifyService;

    /**
     * 处理业务异常（问题8：统一业务异常处理）
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理 @RequestBody 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", message);
        return Result.error(400, message);
    }

    /**
     * 处理 @RequestParam 参数校验异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", message);
        return Result.error(400, message);
    }

    /**
     * 处理表单绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数绑定失败: {}", message);
        return Result.error(400, message);
    }

    /**
     * 处理数据库唯一约束冲突
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicateKeyException(DuplicateKeyException e) {
        log.warn("数据库唯一约束冲突: {}", e.getMessage());
        return Result.error(409, "昵称已存在，请换一个");
    }

    /**
     * 处理其他运行时异常（系统异常，飞书告警）
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.warn("运行时异常: {}", e.getMessage());
        sendErrorAlert(request, e);
        return Result.error(e.getMessage());
    }

    /**
     * 处理其他异常（隐藏内部错误细节，飞书告警）
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常", e);
        sendErrorAlert(request, e);
        return Result.error("系统繁忙，请稍后重试");
    }

    /**
     * 接口异常飞书告警：字段在当前请求线程解析完再交给 @Async 发送
     * （异步线程里 RequestContextHolder 为空，拿不到 request/userId）
     */
    private void sendErrorAlert(HttpServletRequest request, Throwable e) {
        try {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Long userId = UserContext.getCurrentUserId();
            feishuNotifyService.sendErrorAlert(time, request.getRequestURI(), resolveApiName(request),
                    userId != null ? String.valueOf(userId) : "未登录", buildErrorMessage(e));
        } catch (Exception ex) {
            log.error("构建接口异常告警失败", ex);
        }
    }

    /** 取接口名称：与 WebLogAspect 同口径，优先 @ApiName 中文，未标注回退 类名.方法名 */
    private String resolveApiName(HttpServletRequest request) {
        try {
            Object handler = request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                ApiName apiName = handlerMethod.getMethodAnnotation(ApiName.class);
                if (apiName == null) {
                    apiName = handlerMethod.getBeanType().getAnnotation(ApiName.class);
                }
                if (apiName != null) {
                    return apiName.value();
                }
                return handlerMethod.getBeanType().getSimpleName() + "." + handlerMethod.getMethod().getName();
            }
        } catch (Exception ignore) {
            // 解析失败不影响告警
        }
        return request.getRequestURI();
    }

    /** 告警信息：异常类名: message + 堆栈前5行，够定位又不至于刷爆卡片 */
    private String buildErrorMessage(Throwable e) {
        StringBuilder sb = new StringBuilder(e.getClass().getName()).append(": ").append(e.getMessage());
        StackTraceElement[] stack = e.getStackTrace();
        for (int i = 0; i < stack.length && i < 5; i++) {
            sb.append("\n    at ").append(stack[i]);
        }
        return sb.toString();
    }
}
