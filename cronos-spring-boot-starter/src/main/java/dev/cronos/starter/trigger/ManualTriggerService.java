package dev.cronos.starter.trigger;

import dev.cronos.core.domain.JobDescriptor;
import dev.cronos.core.domain.JobExecution;
import dev.cronos.core.model.DiscoveredJob;
import dev.cronos.core.model.JobSourceType;
import dev.cronos.core.model.TriggerSource;
import dev.cronos.core.spi.JobSourceAdapter;
import dev.cronos.starter.config.CronosProperties;
import dev.cronos.starter.discovery.SpringScheduledJobAdapter;
import dev.cronos.starter.persistence.JobDescriptorRepository;
import dev.cronos.starter.tracking.JobExecutionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManualTriggerService {

    private final JobDescriptorRepository jobDescriptorRepository;
    private final JobExecutionService jobExecutionService;
    private final List<JobSourceAdapter> adapters;
    private final SpringScheduledJobAdapter springScheduledJobAdapter;
    private final CronosProperties properties;

    private ExecutorService executorService;

    @PostConstruct
    void initExecutor() {
        executorService = Executors.newFixedThreadPool(properties.getManualTriggerPoolSize(),
                runnable -> {
                    Thread thread = new Thread(runnable);
                    thread.setName("cronos-manual-trigger");
                    thread.setDaemon(true);
                    return thread;
                });
    }

    public TriggerResult trigger(Long jobId) {
        JobDescriptor job = jobDescriptorRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown job id: " + jobId));

        if (jobExecutionService.isRunning(job.getName())) {
            return TriggerResult.alreadyRunning(job.getId(), job.getName());
        }

        JobExecution execution = jobExecutionService.startExecution(job.getName(), TriggerSource.MANUAL);

        executorService.submit(() -> runManualExecution(job, execution.getId()));
        return TriggerResult.started(job.getId(), job.getName(), execution.getId());
    }

    private void runManualExecution(JobDescriptor job, Long executionId) {
        try {
            JobSourceAdapter adapter = adapters.stream()
                    .filter(candidate -> candidate.getSourceType() == job.getSourceType())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No adapter for source: " + job.getSourceType()));

            DiscoveredJob discoveredJob = resolveDiscoveredJob(job);
            adapter.triggerNow(discoveredJob);
            jobExecutionService.completeExecution(executionId);
        } catch (Exception ex) {
            log.warn("Manual trigger failed for job {}: {}", job.getName(), ex.getMessage());
            jobExecutionService.failExecution(executionId, ex);
        }
    }

    private DiscoveredJob resolveDiscoveredJob(JobDescriptor job) {
        if (job.getSourceType() == JobSourceType.SPRING_SCHEDULED) {
            return springScheduledJobAdapter.findDiscoveredJob(job.getName())
                    .orElseGet(() -> fallbackDiscoveredJob(job));
        }
        return fallbackDiscoveredJob(job);
    }

    private DiscoveredJob fallbackDiscoveredJob(JobDescriptor job) {
        return new DiscoveredJob(
                job.getName(),
                job.getBeanName(),
                job.getMethodOrClass(),
                job.getTriggerInfo(),
                null,
                extractMethodName(job.getName())
        );
    }

    private String extractMethodName(String jobName) {
        int separator = jobName.indexOf('#');
        return separator >= 0 ? jobName.substring(separator + 1) : jobName;
    }

    public record TriggerResult(
            Status status,
            Long jobId,
            String jobName,
            Long executionId,
            String message
    ) {
        public enum Status {
            STARTED,
            ALREADY_RUNNING
        }

        public static TriggerResult started(Long jobId, String jobName, Long executionId) {
            return new TriggerResult(Status.STARTED, jobId, jobName, executionId,
                    "Manual execution started");
        }

        public static TriggerResult alreadyRunning(Long jobId, String jobName) {
            return new TriggerResult(Status.ALREADY_RUNNING, jobId, jobName, null,
                    "Job is already running");
        }
    }
}
