package dev.cronos.starter.api.dto;

import dev.cronos.core.model.ExecutionStatus;
import dev.cronos.core.model.TriggerSource;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class JobExecutionResponse {

    private final Long id;
    private final Long jobId;
    private final ExecutionStatus status;
    private final Instant startedAt;
    private final Instant finishedAt;
    private final Long durationMs;
    private final String errorMessage;
    private final TriggerSource triggerSource;
}
