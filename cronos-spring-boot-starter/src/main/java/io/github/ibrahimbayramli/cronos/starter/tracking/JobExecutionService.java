package io.github.ibrahimbayramli.cronos.starter.tracking;

import io.github.ibrahimbayramli.cronos.core.domain.JobDescriptor;
import io.github.ibrahimbayramli.cronos.core.domain.JobExecution;
import io.github.ibrahimbayramli.cronos.core.model.ExecutionStatus;
import io.github.ibrahimbayramli.cronos.core.model.TriggerSource;
import io.github.ibrahimbayramli.cronos.starter.persistence.JobDescriptorRepository;
import io.github.ibrahimbayramli.cronos.starter.persistence.JobExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobExecutionService {

    private final JobDescriptorRepository jobDescriptorRepository;
    private final JobExecutionRepository jobExecutionRepository;

    @Transactional
    public JobExecution startExecution(String jobName, TriggerSource triggerSource) {
        JobDescriptor job = jobDescriptorRepository.findByName(jobName)
                .orElseThrow(() -> new IllegalArgumentException("Unknown job: " + jobName));

        JobExecution execution = JobExecution.builder()
                .job(job)
                .status(ExecutionStatus.RUNNING)
                .startedAt(Instant.now())
                .triggerSource(triggerSource)
                .build();
        return jobExecutionRepository.save(execution);
    }

    @Transactional
    public JobExecution completeExecution(Long executionId) {
        JobExecution execution = jobExecutionRepository.findById(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown execution: " + executionId));
        execution.complete(Instant.now());
        return jobExecutionRepository.save(execution);
    }

    @Transactional
    public JobExecution failExecution(Long executionId, Throwable error) {
        JobExecution execution = jobExecutionRepository.findById(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown execution: " + executionId));
        execution.fail(Instant.now(), summarizeError(error));
        return jobExecutionRepository.save(execution);
    }

    @Transactional(readOnly = true)
    public boolean isRunning(String jobName) {
        return jobDescriptorRepository.findByName(jobName)
                .map(job -> jobExecutionRepository.existsByJobAndStatus(job, ExecutionStatus.RUNNING))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public Optional<JobExecution> getLatestExecution(String jobName) {
        return jobDescriptorRepository.findByName(jobName)
                .flatMap(jobExecutionRepository::findFirstByJobOrderByStartedAtDesc);
    }

    private String summarizeError(Throwable error) {
        if (error == null) {
            return null;
        }
        String message = error.getClass().getSimpleName();
        if (error.getMessage() != null) {
            message += ": " + error.getMessage();
        }
        return message.length() > 4096 ? message.substring(0, 4096) : message;
    }
}
