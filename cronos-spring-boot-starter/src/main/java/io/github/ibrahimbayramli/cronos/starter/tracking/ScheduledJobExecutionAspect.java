package io.github.ibrahimbayramli.cronos.starter.tracking;

import io.github.ibrahimbayramli.cronos.core.model.TriggerSource;
import io.github.ibrahimbayramli.cronos.starter.support.JobNaming;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ScheduledJobExecutionAspect {

    private final JobExecutionService jobExecutionService;
    private final ApplicationContext applicationContext;

    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public Object trackScheduledExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String jobName = JobNaming.resolve(
                applicationContext,
                joinPoint.getTarget(),
                joinPoint.getSignature().getName()
        );

        Long executionId = null;
        try {
            executionId = jobExecutionService.startExecution(jobName, TriggerSource.AUTO).getId();
        } catch (IllegalArgumentException ex) {
            log.debug("Skipping execution tracking for unknown job {}: {}", jobName, ex.getMessage());
            return joinPoint.proceed();
        }

        try {
            Object result = joinPoint.proceed();
            jobExecutionService.completeExecution(executionId);
            return result;
        } catch (Throwable ex) {
            jobExecutionService.failExecution(executionId, ex);
            throw ex;
        }
    }
}
