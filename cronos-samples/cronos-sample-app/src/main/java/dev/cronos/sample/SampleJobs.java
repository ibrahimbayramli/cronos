package dev.cronos.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SampleJobs {

    private static final Logger log = LoggerFactory.getLogger(SampleJobs.class);

    @Scheduled(cron = "0 */5 * * * *")
    public void heartbeat() {
        log.info("Heartbeat job executed");
    }

    @Scheduled(fixedRate = 60_000)
    public void cleanup() {
        log.info("Cleanup job executed");
    }
}
