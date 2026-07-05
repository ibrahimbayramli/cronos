package io.github.ibrahimbayramli.cronos.starter.trigger;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TriggerResult {

    private final Status status;
    private final Long jobId;
    private final String jobName;
    private final Long executionId;
    private final String message;

    public enum Status {
        STARTED,
        ALREADY_RUNNING
    }

    public static TriggerResult started(Long jobId, String jobName, Long executionId) {
        return builder()
                .status(Status.STARTED)
                .jobId(jobId)
                .jobName(jobName)
                .executionId(executionId)
                .message("Manual execution started")
                .build();
    }

    public static TriggerResult alreadyRunning(Long jobId, String jobName) {
        return builder()
                .status(Status.ALREADY_RUNNING)
                .jobId(jobId)
                .jobName(jobName)
                .message("Job is already running")
                .build();
    }
}
