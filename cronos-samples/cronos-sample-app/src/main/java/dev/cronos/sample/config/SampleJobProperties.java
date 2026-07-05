package dev.cronos.sample.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "sample.jobs")
public class SampleJobProperties {

    /**
     * Cron expression for the heartbeat demo job (every 5 minutes by default).
     */
    private String heartbeatCron = "0 */5 * * * *";

    /**
     * Fixed rate for the cleanup demo job.
     */
    private Duration cleanupFixedRate = Duration.ofSeconds(60);
}
