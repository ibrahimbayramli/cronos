package dev.cronos.core.domain;

import dev.cronos.core.model.ExecutionStatus;
import dev.cronos.core.model.TriggerSource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "job_execution")
public class JobExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private JobDescriptor job;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "error_message", length = 4096)
    private String errorMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_source", nullable = false)
    private TriggerSource triggerSource;

    protected JobExecution() {
    }

    public JobExecution(JobDescriptor job, ExecutionStatus status, Instant startedAt,
                        TriggerSource triggerSource) {
        this.job = job;
        this.status = status;
        this.startedAt = startedAt;
        this.triggerSource = triggerSource;
    }

    public Long getId() {
        return id;
    }

    public JobDescriptor getJob() {
        return job;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public TriggerSource getTriggerSource() {
        return triggerSource;
    }

    public void complete(Instant finishedAt) {
        this.finishedAt = finishedAt;
        this.durationMs = finishedAt.toEpochMilli() - startedAt.toEpochMilli();
        this.status = ExecutionStatus.SUCCESS;
    }

    public void fail(Instant finishedAt, String errorMessage) {
        this.finishedAt = finishedAt;
        this.durationMs = finishedAt.toEpochMilli() - startedAt.toEpochMilli();
        this.status = ExecutionStatus.FAILED;
        this.errorMessage = errorMessage;
    }
}
