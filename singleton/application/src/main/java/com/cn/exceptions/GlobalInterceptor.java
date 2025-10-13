package com.cn.exceptions;


import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.util.SaResult;
import com.cn.common.exceptions.RateLimitException;
import com.cn.common.msg.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


/**
 * 全局异常处理
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@RestControllerAdvice
@SuppressWarnings("all")
@Slf4j
@RequiredArgsConstructor
public class GlobalInterceptor {


    @ExceptionHandler(NotLoginException.class)
    public Result handlerException(NotLoginException e) {
        return Result.error("登录信息失效,请重新登录", 401);
    }

    @ExceptionHandler(NotRoleException.class)
    public Result handlerException(NotRoleException e) {
        return Result.error("当前用户权限不足", 401);
    }
    
    @ExceptionHandler(RateLimitException.class)
    public Result handlerException(RateLimitException e) {
        return Result.error(e.getMessage(), 429);
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public SaResult exceptionHandler(Exception e) {
        if (e instanceof MethodArgumentNotValidException) {
            final String message = e.getMessage();
            final List<ObjectError> allErrors = ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors();
            return SaResult.error(allErrors.get(0).getDefaultMessage());
        }
        e.printStackTrace();
        log.error("服务出现了未被拦截异常信息 信息:{}", e.getMessage());
        return SaResult.error(e.getMessage());
    }
}
