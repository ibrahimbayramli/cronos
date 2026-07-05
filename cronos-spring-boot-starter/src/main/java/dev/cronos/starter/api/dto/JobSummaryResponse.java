package dev.cronos.starter.api.dto;

import dev.cronos.core.model.ExecutionStatus;
import dev.cronos.core.model.JobSourceType;
import dev.cronos.core.model.TriggerSource;

import java.time.Instant;

public record JobSummaryResponse(
        Long id,
        String name,
        JobSourceType sourceType,
        String beanName,
        String methodOrClass,
        String triggerInfo,
        Instant discoveredAt,
        boolean enabled,
        Instant lastRunAt,
        ExecutionStatus lastStatus,
        Long lastDurationMs,
        Instant nextRunAt
) {
}
