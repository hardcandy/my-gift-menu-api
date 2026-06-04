package com.wx.gift.aspect;

import com.wx.gift.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Around("execution(* com.wx.gift.controller..*.*(..))")
    public Object logApi(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString().replace("-", "");
        HttpServletRequest request = currentRequest();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        String httpMethod = request == null ? "-" : request.getMethod();
        String uri = request == null ? "-" : request.getRequestURI();
        String query = request == null ? null : request.getQueryString();

        Map<String, Object> params = buildParams(signature.getParameterNames(), joinPoint.getArgs());
        log.info("[{}] request {} {}{} -> {} params={}", requestId, httpMethod, uri, query == null ? "" : "?" + query, methodName, GsonUtil.toJson(params));
        try {
            Object result = joinPoint.proceed();
            long cost = System.currentTimeMillis() - start;
            log.info("[{}] response {} {} cost={}ms body={}", requestId, httpMethod, uri, cost, GsonUtil.toJson(result));
            return result;
        } catch (Throwable throwable) {
            long cost = System.currentTimeMillis() - start;
            log.error("[{}] exception {} {} cost={}ms message={}", requestId, httpMethod, uri, cost, throwable.getMessage(), throwable);
            throw throwable;
        }
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes)) {
            return null;
        }
        return ((ServletRequestAttributes) attributes).getRequest();
    }

    private Map<String, Object> buildParams(String[] names, Object[] args) {
        Map<String, Object> params = new HashMap<>();
        if (args == null || args.length == 0) {
            return params;
        }
        List<Object> anonymous = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse) {
                continue;
            }
            String name = names != null && i < names.length ? names[i] : null;
            if (name == null) {
                anonymous.add(arg);
            } else {
                params.put(name, arg);
            }
        }
        if (!anonymous.isEmpty()) {
            params.put("args", anonymous);
        }
        return params;
    }
}

