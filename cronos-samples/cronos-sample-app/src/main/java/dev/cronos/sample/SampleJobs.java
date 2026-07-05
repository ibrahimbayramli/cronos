package dev.cronos.sample;

import dev.cronos.sample.config.SampleJobProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SampleJobs implements SchedulingConfigurer {

    private final SampleJobProperties properties;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(this::heartbeat, properties.getHeartbeatCron());
        taskRegistrar.addFixedRateTask(this::cleanup, properties.getCleanupFixedRate());
    }

    void heartbeat() {
        log.info("Heartbeat job executed");
    }

    void cleanup() {
        log.info("Cleanup job executed");
    }
}
