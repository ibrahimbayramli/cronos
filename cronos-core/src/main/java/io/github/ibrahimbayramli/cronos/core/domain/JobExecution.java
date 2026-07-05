package io.github.ibrahimbayramli.cronos.core.domain;

import io.github.ibrahimbayramli.cronos.core.model.ExecutionStatus;
import io.github.ibrahimbayramli.cronos.core.model.TriggerSource;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "job_execution")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    public JobExecution complete(Instant finishedAt) {
        this.finishedAt = finishedAt;
        this.durationMs = finishedAt.toEpochMilli() - startedAt.toEpochMilli();
        this.status = ExecutionStatus.SUCCESS;
        return this;
    }

    public JobExecution fail(Instant finishedAt, String errorMessage) {
        this.finishedAt = finishedAt;
        this.durationMs = finishedAt.toEpochMilli() - startedAt.toEpochMilli();
        this.status = ExecutionStatus.FAILED;
        this.errorMessage = errorMessage;
        return this;
    }
}
