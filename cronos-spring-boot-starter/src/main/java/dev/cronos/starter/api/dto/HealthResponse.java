package dev.cronos.starter.api.dto;

import java.time.Instant;

public record HealthResponse(
        String status,
        Instant timestamp,
        long discoveredJobs
) {
}
