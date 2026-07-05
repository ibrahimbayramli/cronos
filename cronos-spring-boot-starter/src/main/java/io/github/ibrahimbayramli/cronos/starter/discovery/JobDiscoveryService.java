package io.github.ibrahimbayramli.cronos.starter.discovery;

import io.github.ibrahimbayramli.cronos.core.domain.JobDescriptor;
import io.github.ibrahimbayramli.cronos.core.domain.JobNextRun;
import io.github.ibrahimbayramli.cronos.core.model.DiscoveredJob;
import io.github.ibrahimbayramli.cronos.core.spi.JobSourceAdapter;
import io.github.ibrahimbayramli.cronos.starter.persistence.JobDescriptorRepository;
import io.github.ibrahimbayramli.cronos.starter.persistence.JobNextRunRepository;
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
            for (DiscoveredJob discovered : adapter.discoverJobs()) {
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

    private void persistDiscoveredJob(JobSourceAdapter adapter, DiscoveredJob discovered) {
        JobDescriptor descriptor = jobDescriptorRepository.findByName(discovered.getName())
                .orElseGet(() -> JobDescriptor.builder()
                        .name(discovered.getName())
                        .sourceType(adapter.getSourceType())
                        .beanName(discovered.getBeanName())
                        .methodOrClass(discovered.getMethodOrClass())
                        .triggerInfo(discovered.getTriggerInfo())
                        .build());

        if (descriptor.getId() == null) {
            descriptor = jobDescriptorRepository.save(descriptor);
        }

        updateNextRun(adapter, discovered, descriptor);
    }

    private void updateNextRun(JobSourceAdapter adapter, JobDescriptor descriptor) {
        updateNextRun(adapter, DiscoveredJob.fromDescriptor(descriptor), descriptor);
    }

    private void updateNextRun(JobSourceAdapter adapter, DiscoveredJob discovered,
                               JobDescriptor descriptor) {
        Instant nextRun = adapter.getNextRunTime(discovered).orElse(null);
        JobNextRun jobNextRun = jobNextRunRepository.findByJob(descriptor)
                .map(existing -> existing.toBuilder().calculatedNextRunAt(nextRun).build())
                .orElseGet(() -> JobNextRun.builder()
                        .job(descriptor)
                        .calculatedNextRunAt(nextRun)
                        .build());
        jobNextRunRepository.save(jobNextRun);
    }
}
