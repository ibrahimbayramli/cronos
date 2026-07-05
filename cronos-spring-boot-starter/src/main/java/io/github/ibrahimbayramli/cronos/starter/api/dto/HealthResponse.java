package io.github.ibrahimbayramli.cronos.starter.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class HealthResponse {

    private final String status;
    private final Instant timestamp;
    private final long discoveredJobs;
}
