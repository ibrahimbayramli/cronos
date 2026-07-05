package io.github.ibrahimbayramli.cronos.starter.api;

import io.github.ibrahimbayramli.cronos.core.domain.JobDescriptor;
import io.github.ibrahimbayramli.cronos.core.domain.JobExecution;
import io.github.ibrahimbayramli.cronos.core.domain.JobNextRun;
import io.github.ibrahimbayramli.cronos.starter.api.dto.JobDetailResponse;
import io.github.ibrahimbayramli.cronos.starter.api.dto.JobExecutionResponse;
import io.github.ibrahimbayramli.cronos.starter.api.dto.JobSummaryResponse;
import io.github.ibrahimbayramli.cronos.starter.persistence.JobDescriptorRepository;
import io.github.ibrahimbayramli.cronos.starter.persistence.JobExecutionRepository;
import io.github.ibrahimbayramli.cronos.starter.persistence.JobNextRunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JobQueryService {

    private final JobDescriptorRepository jobDescriptorRepository;
    private final JobExecutionRepository jobExecutionRepository;
    private final JobNextRunRepository jobNextRunRepository;

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

        return JobSummaryResponse.builder()
                .id(job.getId())
                .name(job.getName())
                .sourceType(job.getSourceType())
                .beanName(job.getBeanName())
                .methodOrClass(job.getMethodOrClass())
                .triggerInfo(job.getTriggerInfo())
                .discoveredAt(job.getDiscoveredAt())
                .enabled(job.isEnabled())
                .lastRunAt(latest != null ? latest.getStartedAt() : null)
                .lastStatus(latest != null ? latest.getStatus() : null)
                .lastDurationMs(latest != null ? latest.getDurationMs() : null)
                .nextRunAt(nextRun != null ? nextRun.getCalculatedNextRunAt() : null)
                .build();
    }

    private JobDetailResponse toDetail(JobDescriptor job) {
        JobSummaryResponse summary = toSummary(job);
        long totalExecutions = jobExecutionRepository.findByJobOrderByStartedAtDesc(job, Pageable.unpaged())
                .getTotalElements();

        return JobDetailResponse.from(summary, totalExecutions);
    }

    private JobExecutionResponse toExecutionResponse(JobExecution execution) {
        return JobExecutionResponse.builder()
                .id(execution.getId())
                .jobId(execution.getJob().getId())
                .status(execution.getStatus())
                .startedAt(execution.getStartedAt())
                .finishedAt(execution.getFinishedAt())
                .durationMs(execution.getDurationMs())
                .errorMessage(execution.getErrorMessage())
                .triggerSource(execution.getTriggerSource())
                .build();
    }
}
