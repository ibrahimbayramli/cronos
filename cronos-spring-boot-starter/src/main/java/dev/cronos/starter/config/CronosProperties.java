package dev.cronos.starter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "cronos")
public class CronosProperties {

    private boolean enabled = true;

    private String apiBasePath = "/cronos/api";

    private Duration executionRetention = Duration.ofDays(90);

    private int manualTriggerPoolSize = 4;
}
