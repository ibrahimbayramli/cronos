package dev.cronos.starter.persistence;

import dev.cronos.core.domain.JobDescriptor;
import dev.cronos.core.domain.JobNextRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobNextRunRepository extends JpaRepository<JobNextRun, Long> {

    Optional<JobNextRun> findByJob(JobDescriptor job);
}
