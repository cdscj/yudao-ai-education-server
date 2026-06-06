package cn.iocoder.yudao.module.ai.framework.config;

import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.ai.service.config.AiStatisticsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AiStatisticsAspect {

    @Resource
    private AiStatisticsService statisticsService;

    /** Pointcut: all methods in AiChatMessageController */
    @Pointcut("execution(* cn.iocoder.yudao.module.ai.controller.admin.chat.AiChatMessageController.sendMessage(..)) || " +
              "execution(* cn.iocoder.yudao.module.ai.controller.admin.chat.AiChatMessageController.sendChatMessageStream(..))")
    public void chatMessagePointcut() {}

    @Around("chatMessagePointcut()")
    public Object aroundChatMessage(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        String apiType = "chat";
        boolean success = true;
        String errorMessage = null;

        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            success = false;
            errorMessage = e.getMessage();
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            try {
                statisticsService.recordApiCall(userId != null ? userId : 0L, null,
                        null, null, apiType, 0, 0, duration, success, errorMessage);
            } catch (Exception ex) {
                log.warn("[aroundChatMessage][记录统计失败]", ex);
            }
        }
    }
}
