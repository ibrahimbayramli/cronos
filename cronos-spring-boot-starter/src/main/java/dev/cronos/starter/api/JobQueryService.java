package dev.cronos.starter.api;

import dev.cronos.core.domain.JobDescriptor;
import dev.cronos.core.domain.JobExecution;
import dev.cronos.core.domain.JobNextRun;
import dev.cronos.core.model.ExecutionStatus;
import dev.cronos.starter.api.dto.JobDetailResponse;
import dev.cronos.starter.api.dto.JobExecutionResponse;
import dev.cronos.starter.api.dto.JobSummaryResponse;
import dev.cronos.starter.persistence.JobDescriptorRepository;
import dev.cronos.starter.persistence.JobExecutionRepository;
import dev.cronos.starter.persistence.JobNextRunRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class JobQueryService {

    private final JobDescriptorRepository jobDescriptorRepository;
    private final JobExecutionRepository jobExecutionRepository;
    private final JobNextRunRepository jobNextRunRepository;

    public JobQueryService(JobDescriptorRepository jobDescriptorRepository,
                           JobExecutionRepository jobExecutionRepository,
                           JobNextRunRepository jobNextRunRepository) {
        this.jobDescriptorRepository = jobDescriptorRepository;
        this.jobExecutionRepository = jobExecutionRepository;
        this.jobNextRunRepository = jobNextRunRepository;
    }

    public List<JobSummaryResponse> listJobs() {
        return jobDescriptorRepository.findAll().stream()
                .map(this::toSummary)
                .toList();
    }

    public JobDetailResponse getJob(Long id) {
        JobDescriptor job = jobDescriptorRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException(id));
        return toDetail(job);
    }

    public Page<JobExecutionResponse> getExecutions(Long jobId, Pageable pageable) {
        JobDescriptor job = jobDescriptorRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId));
        return jobExecutionRepository.findByJobOrderByStartedAtDesc(job, pageable)
                .map(this::toExecutionResponse);
    }

    private JobSummaryResponse toSummary(JobDescriptor job) {
        JobExecution latest = jobExecutionRepository.findFirstByJobOrderByStartedAtDesc(job).orElse(null);
        JobNextRun nextRun = jobNextRunRepository.findByJob(job).orElse(null);
        return new JobSummaryResponse(
                job.getId(),
                job.getName(),
                job.getSourceType(),
                job.getBeanName(),
                job.getMethodOrClass(),
                job.getTriggerInfo(),
                job.getDiscoveredAt(),
                job.isEnabled(),
                latest != null ? latest.getStartedAt() : null,
                latest != null ? latest.getStatus() : null,
                latest != null ? latest.getDurationMs() : null,
                nextRun != null ? nextRun.getCalculatedNextRunAt() : null
        );
    }

    private JobDetailResponse toDetail(JobDescriptor job) {
        JobSummaryResponse summary = toSummary(job);
        long totalExecutions = jobExecutionRepository.findByJobOrderByStartedAtDesc(job, Pageable.unpaged())
                .getTotalElements();
        return new JobDetailResponse(
                summary.id(),
                summary.name(),
                summary.sourceType(),
                summary.beanName(),
                summary.methodOrClass(),
                summary.triggerInfo(),
                summary.discoveredAt(),
                summary.enabled(),
                summary.lastRunAt(),
                summary.lastStatus(),
                summary.lastDurationMs(),
                summary.nextRunAt(),
                totalExecutions
        );
    }

    private JobExecutionResponse toExecutionResponse(JobExecution execution) {
        return new JobExecutionResponse(
                execution.getId(),
                execution.getJob().getId(),
                execution.getStatus(),
                execution.getStartedAt(),
                execution.getFinishedAt(),
                execution.getDurationMs(),
                execution.getErrorMessage(),
                execution.getTriggerSource()
        );
    }
}
