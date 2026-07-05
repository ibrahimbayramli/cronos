package dev.cronos.starter.tracking;

import dev.cronos.core.model.TriggerSource;
import dev.cronos.core.model.TriggerSource;
import dev.cronos.starter.support.JobNaming;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ScheduledJobExecutionAspect {

    private static final Logger log = LoggerFactory.getLogger(ScheduledJobExecutionAspect.class);

    private final JobExecutionService jobExecutionService;
    private final ApplicationContext applicationContext;

    public ScheduledJobExecutionAspect(JobExecutionService jobExecutionService,
                                       ApplicationContext applicationContext) {
        this.jobExecutionService = jobExecutionService;
        this.applicationContext = applicationContext;
    }

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
