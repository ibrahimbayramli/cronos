package io.github.ibrahimbayramli.cronos.core.model;

import io.github.ibrahimbayramli.cronos.core.domain.JobDescriptor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DiscoveredJob {

    private final String name;
    private final String beanName;
    private final String methodOrClass;
    private final String triggerInfo;
    private final Object target;
    private final String methodName;

    public static DiscoveredJob fromDescriptor(JobDescriptor descriptor) {
        return fromDescriptor(descriptor, null);
    }

    public static DiscoveredJob fromDescriptor(JobDescriptor descriptor, Object target) {
        return builder()
                .name(descriptor.getName())
                .beanName(descriptor.getBeanName())
                .methodOrClass(descriptor.getMethodOrClass())
                .triggerInfo(descriptor.getTriggerInfo())
                .target(target)
                .methodName(extractMethodName(descriptor.getName()))
                .build();
    }

    private static String extractMethodName(String jobName) {
        int separator = jobName.indexOf('#');
        return separator >= 0 ? jobName.substring(separator + 1) : jobName;
    }
}
