package dev.cronos.starter.api.dto;

public record TriggerResponse(
        String status,
        Long jobId,
        String jobName,
        Long executionId,
        String message
) {
}
