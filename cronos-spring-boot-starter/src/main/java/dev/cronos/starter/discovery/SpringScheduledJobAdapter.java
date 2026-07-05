package dev.cronos.starter.discovery;

import dev.cronos.core.model.JobSourceType;
import dev.cronos.core.spi.JobSourceAdapter;
import dev.cronos.starter.support.JobNaming;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.scheduling.config.Task;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringScheduledJobAdapter implements JobSourceAdapter {

    private final ApplicationContext applicationContext;
    private final Map<String, DiscoveredJob> discoveredJobs = new ConcurrentHashMap<>();

    @Override
    public JobSourceType getSourceType() {
        return JobSourceType.SPRING_SCHEDULED;
    }

    @Override
    public List<DiscoveredJob> discoverJobs() {
        discoveredJobs.clear();
        List<DiscoveredJob> jobs = new ArrayList<>();

        applicationContext.getBeansOfType(ScheduledTaskHolder.class).values().forEach(holder -> {
            for (ScheduledTask scheduledTask : holder.getScheduledTasks()) {
                extractJob(scheduledTask).ifPresent(job -> {
                    discoveredJobs.put(job.name(), job);
                    jobs.add(job);
                });
            }
        });

        log.info("Discovered {} Spring @Scheduled job(s)", jobs.size());
        return jobs;
    }

    @Override
    public Optional<Instant> getNextRunTime(DiscoveredJob job) {
        return NextRunCalculator.calculate(job.triggerInfo());
    }

    @Override
    public void triggerNow(DiscoveredJob job) {
        Object target = job.target();
        if (target == null && job.beanName() != null) {
            target = applicationContext.getBean(job.beanName());
        }
        String methodName = job.methodName();
        if (target == null || methodName == null) {
            throw new IllegalStateException("Cannot trigger job without target reference: " + job.name());
        }
        try {
            Method method = findMethod(target.getClass(), methodName);
            method.setAccessible(true);
            method.invoke(target);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to invoke scheduled method for job: " + job.name(), ex);
        }
    }

    public Optional<DiscoveredJob> findDiscoveredJob(String name) {
        return Optional.ofNullable(discoveredJobs.get(name));
    }

    private Optional<DiscoveredJob> extractJob(ScheduledTask scheduledTask) {
        Task task = scheduledTask.getTask();
        Runnable runnable = task.getRunnable();
        if (!(runnable instanceof ScheduledMethodRunnable smr)) {
            log.debug("Skipping non-method scheduled task: {}", runnable.getClass().getName());
            return Optional.empty();
        }

        Object target = smr.getTarget();
        Method method = smr.getMethod();
        String beanName = JobNaming.resolveBeanName(applicationContext, target);
        String jobName = JobNaming.resolve(applicationContext, target, method);
        String triggerInfo = describeTrigger(method);

        return Optional.of(new DiscoveredJob(
                jobName,
                beanName,
                method.getDeclaringClass().getSimpleName() + "." + method.getName(),
                triggerInfo,
                target,
                method.getName()
        ));
    }

    private String describeTrigger(Method method) {
        Scheduled scheduled = method.getAnnotation(Scheduled.class);
        if (scheduled == null) {
            return "unknown";
        }
        if (!scheduled.cron().isBlank()) {
            return "cron=" + scheduled.cron();
        }
        if (scheduled.fixedRate() > 0) {
            return "fixedRate=" + scheduled.fixedRate() + "ms";
        }
        if (scheduled.fixedRateString() != null && !scheduled.fixedRateString().isBlank()) {
            return "fixedRate=" + scheduled.fixedRateString();
        }
        if (scheduled.fixedDelay() > 0) {
            return "fixedDelay=" + scheduled.fixedDelay() + "ms";
        }
        if (scheduled.fixedDelayString() != null && !scheduled.fixedDelayString().isBlank()) {
            return "fixedDelay=" + scheduled.fixedDelayString();
        }
        return "unknown";
    }

    private Method findMethod(Class<?> type, String methodName) throws NoSuchMethodException {
        for (Method method : type.getMethods()) {
            if (method.getName().equals(methodName) && method.getParameterCount() == 0) {
                return method;
            }
        }
        throw new NoSuchMethodException(methodName);
    }
}
