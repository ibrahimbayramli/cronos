package dev.cronos.starter.api;

import dev.cronos.starter.api.dto.HealthResponse;
import dev.cronos.starter.api.dto.JobDetailResponse;
import dev.cronos.starter.api.dto.JobExecutionResponse;
import dev.cronos.starter.api.dto.JobSummaryResponse;
import dev.cronos.starter.api.dto.TriggerResponse;
import dev.cronos.starter.persistence.JobDescriptorRepository;
import dev.cronos.starter.trigger.ManualTriggerService;
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
public class JobController {

    private final JobQueryService jobQueryService;
    private final ManualTriggerService manualTriggerService;
    private final JobDescriptorRepository jobDescriptorRepository;

    public JobController(JobQueryService jobQueryService,
                         ManualTriggerService manualTriggerService,
                         JobDescriptorRepository jobDescriptorRepository) {
        this.jobQueryService = jobQueryService;
        this.manualTriggerService = manualTriggerService;
        this.jobDescriptorRepository = jobDescriptorRepository;
    }

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
        ManualTriggerService.TriggerResult result = manualTriggerService.trigger(id);
        return new TriggerResponse(
                result.status().name(),
                result.jobId(),
                result.jobName(),
                result.executionId(),
                result.message()
        );
    }

    @GetMapping("/health")
    public HealthResponse health() {
        return new HealthResponse(
                "UP",
                Instant.now(),
                jobDescriptorRepository.count()
        );
    }

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<String> handleNotFound(JobNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
