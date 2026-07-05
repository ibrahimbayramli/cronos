package dev.cronos.core.spi;

import dev.cronos.core.model.DiscoveredJob;
import dev.cronos.core.model.JobSourceType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Plugin interface for discovering and managing jobs from different scheduling libraries.
 */
public interface JobSourceAdapter {

    JobSourceType getSourceType();

    List<DiscoveredJob> discoverJobs();

    Optional<Instant> getNextRunTime(DiscoveredJob job);

    void triggerNow(DiscoveredJob job);
}
