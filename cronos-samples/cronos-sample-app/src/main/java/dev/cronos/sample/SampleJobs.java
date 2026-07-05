package dev.cronos.sample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SampleJobs {

    @Scheduled(cron = "${sample.jobs.heartbeat-cron:0 */5 * * * *}")
    public void heartbeat() {
        log.info("Heartbeat job executed");
    }

    @Scheduled(fixedRateString = "${sample.jobs.cleanup-fixed-rate:60000}")
    public void cleanup() {
        log.info("Cleanup job executed");
    }
}
