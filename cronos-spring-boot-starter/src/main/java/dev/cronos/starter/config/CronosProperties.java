package dev.cronos.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "cronos")
public class CronosProperties {

    private boolean enabled = true;

    private String apiBasePath = "/cronos/api";

    private Duration executionRetention = Duration.ofDays(90);

    private int manualTriggerPoolSize = 4;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getApiBasePath() {
        return apiBasePath;
    }

    public void setApiBasePath(String apiBasePath) {
        this.apiBasePath = apiBasePath;
    }

    public Duration getExecutionRetention() {
        return executionRetention;
    }

    public void setExecutionRetention(Duration executionRetention) {
        this.executionRetention = executionRetention;
    }

    public int getManualTriggerPoolSize() {
        return manualTriggerPoolSize;
    }

    public void setManualTriggerPoolSize(int manualTriggerPoolSize) {
        this.manualTriggerPoolSize = manualTriggerPoolSize;
    }
}
