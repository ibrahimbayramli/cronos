package dev.cronos.starter.discovery;

import dev.cronos.core.domain.JobDescriptor;
import dev.cronos.core.domain.JobNextRun;
import dev.cronos.core.spi.JobSourceAdapter;
import dev.cronos.starter.persistence.JobDescriptorRepository;
import dev.cronos.starter.persistence.JobNextRunRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobDiscoveryService implements SmartInitializingSingleton {

    private final List<JobSourceAdapter> adapters;
    private final JobDescriptorRepository jobDescriptorRepository;
    private final JobNextRunRepository jobNextRunRepository;

    @Override
    @Transactional
    public void afterSingletonsInstantiated() {
        log.info("Starting Cronos job discovery...");
        for (JobSourceAdapter adapter : adapters) {
            for (JobSourceAdapter.DiscoveredJob discovered : adapter.discoverJobs()) {
                persistDiscoveredJob(adapter, discovered);
            }
        }
    }

    @Transactional
    public void refreshNextRunTimes() {
        for (JobDescriptor job : jobDescriptorRepository.findAll()) {
            adapters.stream()
                    .filter(adapter -> adapter.getSourceType() == job.getSourceType())
                    .findFirst()
                    .ifPresent(adapter -> updateNextRun(adapter, job));
        }
    }

    private void persistDiscoveredJob(JobSourceAdapter adapter, JobSourceAdapter.DiscoveredJob discovered) {
        JobDescriptor descriptor = jobDescriptorRepository.findByName(discovered.name())
                .orElseGet(() -> {
                    JobDescriptor created = new JobDescriptor();
                    created.setName(discovered.name());
                    created.setSourceType(adapter.getSourceType());
                    created.setBeanName(discovered.beanName());
                    created.setMethodOrClass(discovered.methodOrClass());
                    created.setTriggerInfo(discovered.triggerInfo());
                    return created;
                });

        if (descriptor.getId() == null) {
            descriptor = jobDescriptorRepository.save(descriptor);
        }

        updateNextRun(adapter, discovered, descriptor);
    }

    private void updateNextRun(JobSourceAdapter adapter, JobDescriptor descriptor) {
        JobSourceAdapter.DiscoveredJob discovered = new JobSourceAdapter.DiscoveredJob(
                descriptor.getName(),
                descriptor.getBeanName(),
                descriptor.getMethodOrClass(),
                descriptor.getTriggerInfo(),
                null,
                extractMethodName(descriptor.getName())
        );
        updateNextRun(adapter, discovered, descriptor);
    }

    private void updateNextRun(JobSourceAdapter adapter, JobSourceAdapter.DiscoveredJob discovered,
                               JobDescriptor descriptor) {
        Instant nextRun = adapter.getNextRunTime(discovered).orElse(null);
        JobNextRun jobNextRun = jobNextRunRepository.findByJob(descriptor)
                .orElseGet(() -> {
                    JobNextRun created = new JobNextRun();
                    created.setJob(descriptor);
                    return created;
                });
        jobNextRun.setCalculatedNextRunAt(nextRun);
        jobNextRunRepository.save(jobNextRun);
    }

    private String extractMethodName(String jobName) {
        int separator = jobName.indexOf('#');
        return separator >= 0 ? jobName.substring(separator + 1) : jobName;
    }
}
