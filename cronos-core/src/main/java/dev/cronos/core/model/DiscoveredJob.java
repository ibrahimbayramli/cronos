package dev.cronos.core.model;

public record DiscoveredJob(
        String name,
        String beanName,
        String methodOrClass,
        String triggerInfo,
        Object target,
        String methodName
) {
}
