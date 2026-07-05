package dev.cronos.starter.api.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TriggerResponse {

    private final String status;
    private final Long jobId;
    private final String jobName;
    private final Long executionId;
    private final String message;
}
