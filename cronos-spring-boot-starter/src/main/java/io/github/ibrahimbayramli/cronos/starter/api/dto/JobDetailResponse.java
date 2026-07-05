package io.github.ibrahimbayramli.cronos.starter.api.dto;

import io.github.ibrahimbayramli.cronos.core.model.ExecutionStatus;
import io.github.ibrahimbayramli.cronos.core.model.JobSourceType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class JobDetailResponse {

    private final Long id;
    private final String name;
    private final JobSourceType sourceType;
    private final String beanName;
    private final String methodOrClass;
    private final String triggerInfo;
    private final Instant discoveredAt;
    private final boolean enabled;
    private final Instant lastRunAt;
    private final ExecutionStatus lastStatus;
    private final Long lastDurationMs;
    private final Instant nextRunAt;
    private final long totalExecutions;

    public static JobDetailResponse from(JobSummaryResponse summary, long totalExecutions) {
        return builder()
                .id(summary.getId())
                .name(summary.getName())
                .sourceType(summary.getSourceType())
                .beanName(summary.getBeanName())
                .methodOrClass(summary.getMethodOrClass())
                .triggerInfo(summary.getTriggerInfo())
                .discoveredAt(summary.getDiscoveredAt())
                .enabled(summary.isEnabled())
                .lastRunAt(summary.getLastRunAt())
                .lastStatus(summary.getLastStatus())
                .lastDurationMs(summary.getLastDurationMs())
                .nextRunAt(summary.getNextRunAt())
                .totalExecutions(totalExecutions)
                .build();
    }
}
