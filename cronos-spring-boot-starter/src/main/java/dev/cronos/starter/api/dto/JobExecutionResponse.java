package dev.cronos.starter.api.dto;

import dev.cronos.core.model.ExecutionStatus;
import dev.cronos.core.model.TriggerSource;

import java.time.Instant;

public record JobExecutionResponse(
        Long id,
        Long jobId,
        ExecutionStatus status,
        Instant startedAt,
        Instant finishedAt,
        Long durationMs,
        String errorMessage,
        TriggerSource triggerSource
) {
}
