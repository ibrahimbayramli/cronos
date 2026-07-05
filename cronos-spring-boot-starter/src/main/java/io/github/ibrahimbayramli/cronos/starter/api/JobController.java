package io.github.ibrahimbayramli.cronos.starter.api;

import io.github.ibrahimbayramli.cronos.starter.api.dto.HealthResponse;
import io.github.ibrahimbayramli.cronos.starter.api.dto.JobDetailResponse;
import io.github.ibrahimbayramli.cronos.starter.api.dto.JobExecutionResponse;
import io.github.ibrahimbayramli.cronos.starter.api.dto.JobSummaryResponse;
import io.github.ibrahimbayramli.cronos.starter.api.dto.TriggerResponse;
import io.github.ibrahimbayramli.cronos.starter.persistence.JobDescriptorRepository;
import io.github.ibrahimbayramli.cronos.starter.trigger.ManualTriggerService;
import io.github.ibrahimbayramli.cronos.starter.trigger.TriggerResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("${cronos.api-base-path:/cronos/api}")
@RequiredArgsConstructor
public class JobController {

    private final JobQueryService jobQueryService;
    private final ManualTriggerService manualTriggerService;
    private final JobDescriptorRepository jobDescriptorRepository;

    @GetMapping("/jobs")
    public List<JobSummaryResponse> listJobs() {
        return jobQueryService.listJobs();
    }

    @GetMapping("/jobs/{id}")
    public JobDetailResponse getJob(@PathVariable("id") Long id) {
        return jobQueryService.getJob(id);
    }

    @GetMapping("/jobs/{id}/executions")
    public Page<JobExecutionResponse> getExecutions(@PathVariable("id") Long id, Pageable pageable) {
        return jobQueryService.getExecutions(id, pageable);
    }

    @PostMapping("/jobs/{id}/trigger")
    public TriggerResponse triggerJob(@PathVariable("id") Long id) {
        TriggerResult result = manualTriggerService.trigger(id);
        return TriggerResponse.builder()
                .status(result.getStatus().name())
                .jobId(result.getJobId())
                .jobName(result.getJobName())
                .executionId(result.getExecutionId())
                .message(result.getMessage())
                .build();
    }

    @GetMapping("/health")
    public HealthResponse health() {
        return HealthResponse.builder()
                .status("UP")
                .timestamp(Instant.now())
                .discoveredJobs(jobDescriptorRepository.count())
                .build();
    }

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<String> handleNotFound(JobNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
