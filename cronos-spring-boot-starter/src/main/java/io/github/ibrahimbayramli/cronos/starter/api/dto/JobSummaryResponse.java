package io.github.ibrahimbayramli.cronos.starter.api.dto;

import io.github.ibrahimbayramli.cronos.core.model.ExecutionStatus;
import io.github.ibrahimbayramli.cronos.core.model.JobSourceType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class JobSummaryResponse {

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
}
